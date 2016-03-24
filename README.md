# japanese-puzzle
Solve Japanese crossword puzzles (nonograms) automatically. They are visual puzzles where we are given a set of numbers on rows and columns, and the solution forms a black-white image. Japanese puzzles have many names; please see [Wikipedia](https://en.wikipedia.org/wiki/Nonogram)
for a detailed description.

Consider an example solution (*steam train*, 10 x 20) for a puzzle, as found in ``examples/ristikko_06.txt``:

    .......##....#.##.#.
    .....#........####.#
    ...............#.#..
    ....#..####.........
    ....#..#.#..#######.
    .#######.##.#.#.#.#.
    .##########.#.#.#.#.
    ####################
    ##.#.####.#.#.###.#.
    ..#.#....#...#...#..

The actual problem is to deduce the solution from a sequence of row and column
numbers, each of which represents the length of consecutive blacks on that row/column.
For example, in ``examples/ristikko_06_sequence.txt`` we have the puzzle input for
above solution as follows (see explanation below):

``_ 2  1  2  1
_ 1  4  1
_ 1  1
_ 1  4
_ 1  1  1  7
_ 7  2  1  1  1  1
_ 10  1  1  1  1
_ 20
_ 2  1  4  1  1  3  1
_ 1  1  1  1  1
| 2
| 4
| 3 1
| 4
| 5 1
| 1 4
| 4
| 1 6
| 1 1 3
| 5 1
| 1 4
| 1
| 5
| 1 1 1 1
| 1 5
| 3 1 2
| 2 5
| 2 1 1 1
| 1 5
| 1 1``.

The program solves puzzles, based on row and column sequences, and is called with either
* ``java JapanesePuzzle [inputfile]``, or
* ``java JapanesePuzzle < [inputfile.txt]`` (as stdin)

The program accepts its input in two formats:

1. Give input as a sequence of lengths of consecutive blacks on rows and columns.
For example, see ``examples/ristikko_06_sequence.txt``.
All row sequences are started with an underscore ``_`` and column sequencies with a bar ``|``; all numbers in a sequence should be separated by whitespace. The sequence has rows in top-bottom order, and the numbers are read from left to right; columns are listed from left to right, and numbers are read from top to bottom. 

2. Give input as a black-white solution, from which row sums and column sums are computed. The black and white pixels should use hash symbols ``#`` and dots ``.``, respectively. For example, see ``examples/ristikko_06.txt``.

Since the solution for ``ristikko_06`` is unique, the program returns the correct *steam train* solution for either input format:

    Solution 1:
                           2  4  3  4  5  1  4  1  1  5  1  1  5  1  1  3  2  2  1  1 
                                 1     1  4     6  1  1  4        1  5  1  5  1  5  1 
                                                   3              1     2     1       
                                                                  1           1       
                          ------------------------------------------------------------
     2  1  2  1          | .  .  .  .  .  .  .  #  #  .  .  .  .  #  .  #  #  .  #  . |
     1  4  1             | .  .  .  .  .  #  .  .  .  .  .  .  .  .  #  #  #  #  .  # |
     1  1                | .  .  .  .  .  .  .  .  .  .  .  .  .  .  .  #  .  #  .  . |
     1  4                | .  .  .  .  #  .  .  #  #  #  #  .  .  .  .  .  .  .  .  . |
     1  1  1  7          | .  .  .  .  #  .  .  #  .  #  .  .  #  #  #  #  #  #  #  . |
     7  2  1  1  1  1    | .  #  #  #  #  #  #  #  .  #  #  .  #  .  #  .  #  .  #  . |
    10  1  1  1  1       | .  #  #  #  #  #  #  #  #  #  #  .  #  .  #  .  #  .  #  . |
    20                   | #  #  #  #  #  #  #  #  #  #  #  #  #  #  #  #  #  #  #  # |
     2  1  4  1  1  3  1 | #  #  .  #  .  #  #  #  #  .  #  .  #  .  #  #  #  .  #  . |
     1  1  1  1  1       | .  .  #  .  #  .  .  .  .  #  .  .  .  #  .  .  .  #  .  . |
                          ------------------------------------------------------------

Not all black-white puzzles have a unique solution.
Suppose we have 2 x 2 image with sequence ``_ 1 _ 1 | 1 | 1``; that is,
all rows and columns have exactly one black pixel. The following two images both satisfy this requirement:

    # .   and   . #
    . #         # .

If the solution is not unique, the default search algorithm fails to find any solutions.

The program works for *simple* nonograms, which covers quite a lot of puzzles.
These puzzles are guaranteed to be solvable by humans, too.
The search algorithm does not support guessing and back-tracking, so for
some puzzles the program might miss a unique solution, even if one exists. The problem
of always finding a unique solution is NP-hard, so it's likely that a perfect
solver is neither easy to implement nor fast. In practice, puzzles of
size 50 x 80 may already be out of scope, as it takes too much time to solve them.

The motivation for this program was that I wanted to create Japanese puzzles on my own.
Choosing upon a solution, which is a black-white image, is not enough -- I also needed
to check whether the solution is unique and easy enough to deduce. Doing this manually
would have been a tedious task. Since such a solver was not available,
I decided to write one myself. The program was a success, and a generated quite
a few personalized Japanese puzzles for others to solve.

This project was completed in 2007, so it was written in Java 1.6.

