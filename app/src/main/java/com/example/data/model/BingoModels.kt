package com.example.data.model

data class BingoBall(
  val number: Int,
  val column: Char
) {
  companion object {
    fun fromNumber(n: Int): BingoBall {
      val col = when (n) {
        in 1..15 -> 'B'
        in 16..30 -> 'I'
        in 31..45 -> 'N'
        in 46..60 -> 'G'
        else -> 'O'
      }
      return BingoBall(n, col)
    }
  }
}

data class BingoCell(
  val number: Int,
  val column: Char,
  val isFree: Boolean = false,
  val isDaubed: Boolean = false
)

data class BingoCard(
  val id: String,
  val grid: List<List<BingoCell>>
)

object BingoGenerator {
  fun generateCard(id: String): BingoCard {
    val bList = (1..15).shuffled().take(5)
    val iList = (16..30).shuffled().take(5)
    val nList = (31..45).shuffled().take(4) // 4 numbers + 1 free space
    val gList = (46..60).shuffled().take(5)
    val oList = (61..75).shuffled().take(5)

    val rows = mutableListOf<List<BingoCell>>()
    for (row in 0 until 5) {
      val rowCells = mutableListOf<BingoCell>()
      
      // B column
      rowCells.add(BingoCell(number = bList[row], column = 'B'))
      // I column
      rowCells.add(BingoCell(number = iList[row], column = 'I'))
      // N column (center is FREE)
      if (row == 2) {
        rowCells.add(BingoCell(number = 0, column = 'N', isFree = true, isDaubed = true))
      } else {
        val nIndex = if (row > 2) row - 1 else row
        rowCells.add(BingoCell(number = nList[nIndex], column = 'N'))
      }
      // G column
      rowCells.add(BingoCell(number = gList[row], column = 'G'))
      // O column
      rowCells.add(BingoCell(number = oList[row], column = 'O'))

      rows.add(rowCells)
    }
    return BingoCard(id, rows)
  }

  fun checkWin(card: BingoCard): Boolean {
    val grid = card.grid

    // Check rows
    for (r in 0 until 5) {
      if (grid[r].all { it.isDaubed || it.isFree }) return true
    }

    // Check columns
    for (c in 0 until 5) {
      var colWin = true
      for (r in 0 until 5) {
        if (!grid[r][c].isDaubed && !grid[r][c].isFree) {
          colWin = false
          break
        }
      }
      if (colWin) return true
    }

    // Main diagonal
    var diag1Win = true
    for (i in 0 until 5) {
      if (!grid[i][i].isDaubed && !grid[i][i].isFree) {
        diag1Win = false
        break
      }
    }
    if (diag1Win) return true

    // Anti-diagonal
    var diag2Win = true
    for (i in 0 until 5) {
      if (!grid[i][4 - i].isDaubed && !grid[i][4 - i].isFree) {
        diag2Win = false
        break
      }
    }
    if (diag2Win) return true

    // 4 Corners
    val corners = listOf(grid[0][0], grid[0][4], grid[4][0], grid[4][4])
    if (corners.all { it.isDaubed || it.isFree }) return true

    return false
  }
}
