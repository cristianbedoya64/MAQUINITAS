package com.example

import com.example.data.model.BingoCell
import com.example.data.model.BingoGenerator
import com.example.data.model.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testBingoCardGeneration() {
    val card = BingoGenerator.generateCard("test_card_1")
    assertEquals(5, card.grid.size)
    assertEquals(5, card.grid[0].size)

    // Center cell must be FREE and daubed
    val center = card.grid[2][2]
    assertTrue(center.isFree)
    assertTrue(center.isDaubed)

    // B column checks
    for (r in 0 until 5) {
      val bCell = card.grid[r][0]
      assertEquals('B', bCell.column)
      assertTrue(bCell.number in 1..15)
    }

    // O column checks
    for (r in 0 until 5) {
      val oCell = card.grid[r][4]
      assertEquals('O', oCell.column)
      assertTrue(oCell.number in 61..75)
    }
  }

  @Test
  fun testBingoWinDetection() {
    val card = BingoGenerator.generateCard("win_test")
    // Initially not a win
    assertFalse(BingoGenerator.checkWin(card))

    // Mark row 0
    val winRowGrid = card.grid.mapIndexed { r, row ->
      if (r == 0) row.map { it.copy(isDaubed = true) } else row
    }
    val winCard = card.copy(grid = winRowGrid)
    assertTrue(BingoGenerator.checkWin(winCard))
  }

  @Test
  fun testAdminRecognition() {
    val adminUser = User(
      id = "admin_1",
      email = "cristianalexanderbedoyamarin8@gmail.com",
      role = "user"
    )
    assertTrue(adminUser.isAdmin)

    val roleAdminUser = User(
      id = "admin_2",
      email = "someone@example.com",
      role = "admin"
    )
    assertTrue(roleAdminUser.isAdmin)

    val normalUser = User(
      id = "user_1",
      email = "player@example.com",
      role = "user"
    )
    assertFalse(normalUser.isAdmin)
  }
}
