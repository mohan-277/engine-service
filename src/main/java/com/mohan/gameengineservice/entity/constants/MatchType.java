package com.mohan.gameengineservice.entity.constants;

public enum MatchType {
    T20,oneDay,testMatch;  // 20 overs , 50 overs, 100 overs
}


/*
*  TestMatch rules
*  this game is end till all the 10 wickets down until they play
*  and there is 3 round for the single test and bases on that they will evaluate
*
*   example  1 ind / aus == first match ind score = 100 && aus score is 101
*                           second match aus starts with 101+ current match scores 101+ 100  && inda 100 + 150
*                           third match ind starts with 250 + current match scores 120  && aus 100
*                           so the total calculation the india wins
*
*
* */