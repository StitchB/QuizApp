package com.example.android.quizapp;

interface Constants {

    //Used to keep activity state
    String STATE_RANDOM_QUESTION_NUMBER = "randomQuestionNumber";
    String STATE_ALL_SELECTED_ANSWERS = "allSelectedAnswers";
    String STATE_ALREADY_USED_QUESTIONS = "alreadyUsedQuestions";
    String STATE_QUESTIONS_TO_USE = "questionsToUse";
    String STATE_QUIZ_LAST_QUESTION_TYPE = "quizLastQuestionType";
    String STATE_SUMMARY_CURRENT_QUESTION_TYPE = "summaryCurrentQuestionType";
    String STATE_CURRENT_PLAYER = "currentPlayer";
    String STATE_CURRENT_QUESTION = "currentQuestion";
    String STATE_ANSWERS_TOTAL = "answersTotal";
    String STATE_PLAYER_1_CORRECT_ANSWERS = "player1CorrectAnswers";
    String STATE_PLAYER_2_CORRECT_ANSWERS = "player2CorrectAnswers";
    String STATE_ON_CREATE_QUESTION_GENERATED = "onCreateQuestionGenerated";

    //Hashmap keys
    String QUESTION_KEY_QUESTION = "question";
    String QUESTION_KEY_ANSWER_1 = "answer_1";
    String QUESTION_KEY_ANSWER_2 = "answer_2";
    String QUESTION_KEY_ANSWER_3 = "answer_3";
    String QUESTION_KEY_ANSWER_4 = "answer_4";
    String QUESTION_KEY_ANSWER_TYPE = "answer_type";

    //Question answer types
    String QUESTION_ANSWER_TYPE_SINGLE_CHOICE_RADIO = "answer_type_single_choice_radio";
    String QUESTION_ANSWER_TYPE_SINGLE_ANSWER_TEXT = "answer_type_single_choice_text";
    String QUESTION_ANSWER_TYPE_MULTIPLE_CHOICE_CHECKBOXES = "answer_type_multiple_choice_checkboxes";

    //Maximum of Answers (Total for 2 players)
    int MAX_ANSWERS = 10;
}
