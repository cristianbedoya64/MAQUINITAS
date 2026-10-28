package com.example.ui.screens.games

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.ui.theme.AmberGold
import com.example.ui.theme.HyperCyan
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.RoyalOnSurface
import com.example.ui.theme.RoyalSurfaceContainer
import com.example.ui.theme.RoyalSurfaceContainerHigh

data class PlayingCard(
  val rank: Int, // 2..14 (14 = Ace)
  val suit: Char, // 'S' (♠), 'H' (♥), 'D' (♦), 'C' (♣)
  val isHeld: Boolean = false
) {
  val rankName: String
    get() = when (rank) {
      14 -> "A"
      13 -> "K"
      12 -> "Q"
      11 -> "J"
      else -> rank.toString()
    }

  val suitSymbol: String
    get() = when (suit) {
      'S' -> "♠"
      'H' -> "♥"
      'D' -> "♦"
      else -> "♣"
    }

  val suitColor: Color
    get() = if (suit == 'H' || suit == 'D') Color(0xFFDC2626) else Color(0xFF1E293B)
}

enum class PokerPhase {
  BETTING,
  HOLDING,
  FINISHED
}

@Composable
fun DrawPokerGame(
  user: User?,
  onReward: (coinsDelta: Long, xpGain: Long, desc: String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var betAmount by remember { mutableLongStateOf(100L) }
  var phase by remember { mutableStateOf(PokerPhase.BETTING) }
  var hand = remember { mutableStateListOf<PlayingCard>() }
  var deck by remember { mutableStateOf(generateDeck().shuffled().toMutableList()) }
  var resultMsg by remember { mutableStateOf("¡Elige tu apuesta y reparte 5 cartas!") }
  var winMultiplier by remember { mutableIntStateOf(0) }

  fun vibrate(duration: Long) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vm?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        @Suppress("DEPRECATION")
        v?.vibrate(duration)
      }
    } catch (_: Exception) {}
  }

  fun dealHand() {
    if ((user?.coins ?: 0) < betAmount) {
      resultMsg = "⚠️ ¡No tienes suficientes monedas para apostar $betAmount!"
      return
    }
    vibrate(40)
    deck = generateDeck().shuffled().toMutableList()
    hand.clear()
    repeat(5) {
      hand.add(deck.removeAt(0))
    }
    phase = PokerPhase.HOLDING
    resultMsg = "Toca las cartas que desees MANTENER (HOLD) y pulsa CAMBIAR."
  }

  fun toggleHold(index: Int) {
    if (phase != PokerPhase.HOLDING) return
    vibrate(25)
    val card = hand[index]
    hand[index] = card.copy(isHeld = !card.isHeld)
  }

  fun drawCards() {
    if (phase != PokerPhase.HOLDING) return
    vibrate(60)
    for (i in 0 until 5) {
      if (!hand[i].isHeld) {
        hand[i] = deck.removeAt(0)
      }
    }
    phase = PokerPhase.FINISHED

    // Evaluate Hand
    val outcome = evaluatePokerHand(hand.toList())
    winMultiplier = outcome.multiplier

    if (outcome.multiplier > 0) {
      vibrate(120)
      val totalPrize = betAmount * outcome.multiplier
      val netProfit = totalPrize - betAmount
      val xp = (outcome.multiplier * 25).toLong()
      resultMsg = "🎉 ¡${outcome.name}! Ganaste $totalPrize Coins (${outcome.multiplier}x)"
      onReward(netProfit, xp, "Draw Poker: ${outcome.name}")
    } else {
      resultMsg = "Sin jugada premiada. ¡Inténtalo de nuevo!"
      onReward(-betAmount, 10, "Draw Poker")
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF13062A))
      .verticalScroll(rememberScrollState())
      .padding(14.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Header Banner
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(Color(0xFF1E1035))
        .border(1.5.dp, RadiantGold, RoundedCornerShape(12.dp))
        .padding(vertical = 10.dp, horizontal = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Icon(Icons.Default.Casino, contentDescription = null, tint = RadiantGold, modifier = Modifier.size(22.dp))
      Spacer(modifier = Modifier.width(8.dp))
      Text("ROYAL DRAW POKER VIP", fontWeight = FontWeight.Black, fontSize = 15.sp, color = RadiantGold)
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Paytable Quick Bar
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1033)),
      shape = RoundedCornerShape(12.dp)
    ) {
      Column(modifier = Modifier.padding(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Escala Real: 800x", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RadiantGold)
          Text("Póker: 25x", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = HyperCyan)
          Text("Full House: 9x", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AmberGold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Color: 6x", fontSize = 10.sp, color = RoyalOnSurface)
          Text("Escalera: 4x", fontSize = 10.sp, color = RoyalOnSurface)
          Text("Trío: 3x", fontSize = 10.sp, color = RoyalOnSurface)
          Text("Doble Par: 2x", fontSize = 10.sp, color = RoyalOnSurface)
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 5 Cards Display
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      if (hand.isEmpty()) {
        repeat(5) {
          BlankCardSlot(modifier = Modifier.weight(1f))
        }
      } else {
        hand.forEachIndexed { index, card ->
          PokerCardView(
            card = card,
            onClick = { toggleHold(index) },
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Result Alert Box
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(Color(0xFF211538))
        .border(1.dp, if (winMultiplier > 0) RadiantGold else Color(0x33FFFFFF), RoundedCornerShape(10.dp))
        .padding(horizontal = 12.dp, vertical = 10.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = resultMsg,
        color = if (winMultiplier > 0) RadiantGold else Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        textAlign = TextAlign.Center
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Bet Selector
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("Apuesta:", color = AmberGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        listOf(50L, 100L, 250L, 500L).forEach { amount ->
          val isSelected = betAmount == amount
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(if (isSelected) AmberGold else Color(0xFF2A1752))
              .border(1.dp, if (isSelected) RadiantGold else Color(0x33FFFFFF), RoundedCornerShape(8.dp))
              .clickable(enabled = phase != PokerPhase.HOLDING) { betAmount = amount }
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(
              text = "$amount",
              fontSize = 11.sp,
              fontWeight = FontWeight.Black,
              color = if (isSelected) Color(0xFF472A00) else Color.White
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Action Buttons (Deal or Draw)
    if (phase == PokerPhase.HOLDING) {
      Button(
        onClick = { drawCards() },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("poker_draw_button"),
        colors = ButtonDefaults.buttonColors(containerColor = HyperCyan),
        shape = RoundedCornerShape(9999.dp)
      ) {
        Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF003640))
        Spacer(modifier = Modifier.width(6.dp))
        Text("¡DESCARTAR Y CAMBIAR!", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color(0xFF003640))
      }
    } else {
      Button(
        onClick = { dealHand() },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("poker_deal_button"),
        colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
        shape = RoundedCornerShape(9999.dp)
      ) {
        Icon(Icons.Default.Casino, contentDescription = null, tint = Color(0xFF472A00))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = if (phase == PokerPhase.FINISHED) "¡JUGAR OTRA MANO ($betAmount)!" else "¡REPARTIR CARTAS ($betAmount)!",
          fontWeight = FontWeight.Black,
          fontSize = 14.sp,
          color = Color(0xFF472A00)
        )
      }
    }
  }
}

@Composable
fun PokerCardView(
  card: PlayingCard,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Hold Indicator Pill
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(4.dp))
        .background(if (card.isHeld) RadiantGold else Color.Transparent)
        .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
      Text(
        text = if (card.isHeld) "HOLD" else " ",
        fontSize = 9.sp,
        fontWeight = FontWeight.Black,
        color = Color(0xFF472A00)
      )
    }

    Spacer(modifier = Modifier.height(2.dp))

    // The Physical Card Face
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(105.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(Color(0xFFFFFDF7))
        .border(
          width = if (card.isHeld) 2.5.dp else 1.dp,
          color = if (card.isHeld) RadiantGold else Color(0xFFD1D5DB),
          shape = RoundedCornerShape(10.dp)
        )
        .shadow(4.dp, RoundedCornerShape(10.dp))
        .clickable(onClick = onClick)
        .padding(6.dp)
    ) {
      // Top Left Rank & Suit
      Column(modifier = Modifier.align(Alignment.TopStart)) {
        Text(
          text = card.rankName,
          fontSize = 14.sp,
          fontWeight = FontWeight.Black,
          color = card.suitColor,
          lineHeight = 14.sp
        )
        Text(
          text = card.suitSymbol,
          fontSize = 12.sp,
          color = card.suitColor,
          lineHeight = 12.sp
        )
      }

      // Large Center Suit
      Text(
        text = card.suitSymbol,
        fontSize = 28.sp,
        color = card.suitColor,
        modifier = Modifier.align(Alignment.Center)
      )
    }
  }
}

@Composable
fun BlankCardSlot(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier
      .height(105.dp)
      .clip(RoundedCornerShape(10.dp))
      .background(Color(0xFF2A1752))
      .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(10.dp)),
    contentAlignment = Alignment.Center
  ) {
    Text("?", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0x66FFFFFF))
  }
}

fun generateDeck(): List<PlayingCard> {
  val deck = mutableListOf<PlayingCard>()
  val suits = listOf('S', 'H', 'D', 'C')
  for (suit in suits) {
    for (rank in 2..14) {
      deck.add(PlayingCard(rank = rank, suit = suit))
    }
  }
  return deck
}

data class HandOutcome(val name: String, val multiplier: Int)

fun evaluatePokerHand(cards: List<PlayingCard>): HandOutcome {
  if (cards.size < 5) return HandOutcome("Carta Alta", 0)

  val sorted = cards.sortedBy { it.rank }
  val ranks = sorted.map { it.rank }
  val suits = sorted.map { it.suit }

  val isFlush = suits.all { it == suits[0] }
  val isStraight = ranks == listOf(ranks[0], ranks[0] + 1, ranks[0] + 2, ranks[0] + 3, ranks[0] + 4) ||
      ranks == listOf(2, 3, 4, 5, 14) // Ace-low straight

  val rankCounts = ranks.groupingBy { it }.eachCount()
  val countValues = rankCounts.values.sortedDescending()

  // Royal Flush
  if (isFlush && isStraight && ranks.contains(14) && ranks.contains(13)) {
    return HandOutcome("Escalera Real", 800)
  }
  // Straight Flush
  if (isFlush && isStraight) {
    return HandOutcome("Escalera de Color", 50)
  }
  // Four of a kind
  if (countValues == listOf(4, 1)) {
    return HandOutcome("Póker (4 Iguales)", 25)
  }
  // Full House
  if (countValues == listOf(3, 2)) {
    return HandOutcome("Full House", 9)
  }
  // Flush
  if (isFlush) {
    return HandOutcome("Color (Flush)", 6)
  }
  // Straight
  if (isStraight) {
    return HandOutcome("Escalera", 4)
  }
  // Three of a kind
  if (countValues == listOf(3, 1, 1)) {
    return HandOutcome("Trío", 3)
  }
  // Two Pair
  if (countValues == listOf(2, 2, 1)) {
    return HandOutcome("Doble Pareja", 2)
  }
  // Jacks or Better
  if (countValues == listOf(2, 1, 1, 1)) {
    val pairRank = rankCounts.entries.first { it.value == 2 }.key
    if (pairRank >= 11) { // J, Q, K, A
      return HandOutcome("Pareja de Jotas o Mejor", 1)
    }
  }

  return HandOutcome("Sin Premio", 0)
}
