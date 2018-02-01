package com.example.android.quizapp;

interface Constants {

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
    String QUESTION_ANSWER_TYPE_MULIPLE_CHOICE_CHECKBOXES = "answer_type_multiple_choice_checkboxes";

    //Maximum of Answers (Total for 2 players)
    int MAX_ANSWERS = 10;
}
