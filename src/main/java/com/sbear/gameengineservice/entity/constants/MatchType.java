package com.sbear.gameengineservice.entity.constants;

public enum MatchType {
    T20,ONE_DAY,TEST_MATCH;  // 20 overs , 50 overs, 100 overs
}


/**
 * Test Match Rules:
 *
 * - A Test Match continues until all 10 wickets are lost for each team.
 * - Each team gets to bat twice and bowl twice.
 * - The team with the highest total score across all innings wins the match.
 *
 * **Example: **
 * 1. **First Innings: **
 *    - India scores 100 runs.
 *    - Australia scores 101 runs.
 *
 * 2. **Second Innings: **
 *    - Australia starts with a total of 101 runs from their first innings.
 *    - India scores 150 runs in their second innings.
 *    - Australia adds their second innings score to their previous total.
 *    - India adds their second innings score to their previous total.
 *
 * 3. **Third Innings: **
 *    - India starts their third innings with their total from previous innings.
 *    - Australia scores 100 runs in their third innings.
 *    - India adds their third innings score to their total.
 *    - Australia adds their third innings score to their total.
 *
 * **Result: **
 * - The team with the highest total score after all innings is the winner.
 * - In this example, India’s final score is higher than Australia’s, so India wins.
 */
