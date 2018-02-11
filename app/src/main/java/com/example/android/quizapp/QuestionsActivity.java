package com.example.android.quizapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import net.cachapa.expandablelayout.ExpandableLayout;
import com.badoualy.stepperindicator.StepperIndicator;
import com.shashank.sony.fancytoastlib.FancyToast;

public class QuestionsActivity extends MainActivity implements OnClickListener {

    //Current context
    private Context context;

    //Hashmaps for keeping questions & answers
    private final HashMap<String, HashMap<String, String>> questions = new HashMap<>();
    private final HashMap<String, ArrayList> answers = new HashMap<>();
    private HashMap<String, HashMap<String, String>> allSelectedAnswers = new HashMap<>();
    private ArrayList<String> alreadyUsedQuestions = new ArrayList<>();

    //Question numbers per each answer type
    private final String[] singleChoiceRadioQuestions = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "13", "17", "20", "23",
            "25", "26", "28", "30", "31", "33", "35", "36", "37", "39", "41", "43", "46",
            "47", "48", "49", "50", "52"};
    private final String[] multipleChoiceCheckboxQuestions = {"11", "15", "16", "18", "22", "24", "29", "34", "44", "54", "55"};
    private final String[] singleAnswerTextQuestions = {"12", "14", "19", "21", "27", "32", "38", "40", "42", "45", "51", "53"};

    //Current random question number
    private String randomQuestionNumber;

    //Total questions count
    private final int questionsCount = 55;

    //Array list to keep all questions indexes (it will be used later to generate random questions)
    private ArrayList<String> questionsToUse = new ArrayList<>();

    //Last used question types for the 'Quiz' & 'Answers Summary'
    private String quizLastQuestionType;
    private String summaryCurrentQuestionType;

    //Additional counters
    private int currentPlayer = 1;
    private int currentQuestion = 0;
    private int answersTotal = 0;
    private int player1CorrectAnswers = 0, player2CorrectAnswers = 0;

    //Flag to keep info if current random question was generated in onCreate method
    private boolean onCreateQuestionGenerated = false;

    //Main heading & continue button views
    private TextView mainHeadingView;
    private ImageView buttonContinue;

    //Questions & Answer views
    private TextView questionView;
    private RadioGroup answerRadiosView;
    private View answerCheckboxesView;
    private EditText answerEditTextView;
    private RadioButton answer1RadioView, answer2RadioView, answer3RadioView, answer4RadioView;
    private CheckBox answer1CheckBoxView, answer2CheckBoxView, answer3CheckBoxView, answer4CheckBoxView;

    //'Next Player' card view
    private CardView nextPlayerView;
    private TextView nextPlayerInfoView;

    //'Answers Summary' card view
    private CardView answersSummaryView;
    private ExpandableLayout answersSummary1, answersSummary2, answersSummary3, answersSummary4, answersSummary5;
    private ImageView indicatorQuestion1Player1, indicatorQuestion1Player2, indicatorQuestion2Player1, indicatorQuestion2Player2,
            indicatorQuestion3Player1, indicatorQuestion3Player2, indicatorQuestion4Player1, indicatorQuestion4Player2,
            indicatorQuestion5Player1, indicatorQuestion5Player2;

    //Stepper indicator
    private StepperIndicator stepperIndicator;

    //Restored instance state flag
    private Boolean restoredInstanceState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Call super class implementation of onCreate method
        super.onCreate(savedInstanceState);

        //Set 'activity_questions' as a main design file
        setContentView(R.layout.activity_questions);

        //Set application context
        context = getApplicationContext();

        //Background
        ImageView background = findViewById(R.id.background);
        loadGlideImage(context, R.drawable.bg2, background);

        //Main heading
        mainHeadingView = findViewById(R.id.main_heading);

        //Stepper indicator
        stepperIndicator = findViewById(R.id.stepper_indicator);

        //Question & answer views
        questionView = findViewById(R.id.question);
        answerRadiosView = findViewById(R.id.answers_r);
        answerCheckboxesView = findViewById(R.id.answers_c);
        answerEditTextView = findViewById(R.id.answer_e);
        answer1RadioView = findViewById(R.id.answer_1_r);
        answer2RadioView = findViewById(R.id.answer_2_r);
        answer3RadioView = findViewById(R.id.answer_3_r);
        answer4RadioView = findViewById(R.id.answer_4_r);
        answer1CheckBoxView = findViewById(R.id.answer_1_c);
        answer2CheckBoxView = findViewById(R.id.answer_2_c);
        answer3CheckBoxView = findViewById(R.id.answer_3_c);
        answer4CheckBoxView = findViewById(R.id.answer_4_c);

        //'Next Player' card view
        nextPlayerView = findViewById(R.id.next_player_card_view);
        nextPlayerView.setCardBackgroundColor(Color.TRANSPARENT);
        nextPlayerView.setCardElevation(0);
        nextPlayerInfoView = findViewById(R.id.next_player_info);

        //'Answers Summary' card view
        answersSummaryView = findViewById(R.id.answers_summary_card_view);
        answersSummaryView.setCardBackgroundColor(Color.TRANSPARENT);
        answersSummaryView.setCardElevation(0);

        //Create array list with all questions indexes (it will be used later to generate random questions)
        for (int i = 1; i <= questionsCount; i++) {
            questionsToUse.add(String.valueOf(i));
        }

        //Main continue button
        buttonContinue = findViewById(R.id.button_continue);
        loadGlideImage(context, R.drawable.selector_continue_button, buttonContinue);

        //'Next Player' continue button
        ImageView buttonNextPlayer = findViewById(R.id.next_player_continue);
        loadGlideImage(context, R.drawable.selector_continue_button, buttonNextPlayer);

        //Summary 'Reset' continue button
        ImageView buttonReset = findViewById(R.id.reset_button);
        loadGlideImage(context, R.drawable.selector_reset_button, buttonReset);

        //'Next Player' image
        ImageView nextPlayerImage = findViewById(R.id.next_player_image);
        loadGlideImage(context, R.drawable.next_player, nextPlayerImage);

        //Set on click listener event for the continue button
        buttonContinue.setOnClickListener(this);

        //Set questions and the corresponding answers
        setQuestionsData();

        //Restore instance state
        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        }

        //If not restored instance state
        if(!restoredInstanceState) {
            //Get random question
            getRandomQuestion(true);
        } else {
            //Get question info
            getQuestionInfo();

            //Show summary if all question answered already
            if (answersTotal == Constants.MAX_ANSWERS) {
                //Hide 'Continue' button
                buttonContinue.setVisibility(View.INVISIBLE);

                //Inflate 'Answers Summary'
                inflateSummary();
            }
        }

        //If not restored instance state
        if(!restoredInstanceState) {
            //Show 'Next Player' view before first question
            showNextPlayerView(true);
        }

        //Set restoredInstanceState flag to false
        restoredInstanceState = false;
    }

    /**
     * Save instance state
     *
     * @param savedInstanceState - Saved instance state
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //Save the state
        savedInstanceState.putString(Constants.STATE_RANDOM_QUESTION_NUMBER, randomQuestionNumber);
        savedInstanceState.putSerializable(Constants.STATE_ALL_SELECTED_ANSWERS, allSelectedAnswers);
        savedInstanceState.putStringArrayList(Constants.STATE_ALREADY_USED_QUESTIONS, alreadyUsedQuestions);
        savedInstanceState.putStringArrayList(Constants.STATE_QUESTIONS_TO_USE, questionsToUse);
        savedInstanceState.putString(Constants.STATE_QUIZ_LAST_QUESTION_TYPE, quizLastQuestionType);
        savedInstanceState.putString(Constants.STATE_SUMMARY_CURRENT_QUESTION_TYPE, summaryCurrentQuestionType);
        savedInstanceState.putInt(Constants.STATE_CURRENT_PLAYER, currentPlayer);
        savedInstanceState.putInt(Constants.STATE_CURRENT_QUESTION, currentQuestion);
        savedInstanceState.putInt(Constants.STATE_ANSWERS_TOTAL, answersTotal);
        savedInstanceState.putInt(Constants.STATE_PLAYER_1_CORRECT_ANSWERS, player1CorrectAnswers);
        savedInstanceState.putInt(Constants.STATE_PLAYER_2_CORRECT_ANSWERS, player2CorrectAnswers);
        savedInstanceState.putBoolean(Constants.STATE_ON_CREATE_QUESTION_GENERATED, onCreateQuestionGenerated);

        //Call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Restore instance state
     *
     * @param savedInstanceState - Saved instance state
     */
    public void restoreInstanceState(Bundle savedInstanceState) {
        //Set restoredInstanceState flag to false
        restoredInstanceState = true;

        //Restore the state from saved instance
        randomQuestionNumber = savedInstanceState.getString(Constants.STATE_RANDOM_QUESTION_NUMBER);
        allSelectedAnswers = (HashMap<String, HashMap<String, String>>) savedInstanceState.getSerializable(Constants.STATE_ALL_SELECTED_ANSWERS);
        alreadyUsedQuestions = savedInstanceState.getStringArrayList(Constants.STATE_ALREADY_USED_QUESTIONS);
        questionsToUse = savedInstanceState.getStringArrayList(Constants.STATE_QUESTIONS_TO_USE);
        quizLastQuestionType = savedInstanceState.getString(Constants.STATE_QUIZ_LAST_QUESTION_TYPE);
        summaryCurrentQuestionType = savedInstanceState.getString(Constants.STATE_SUMMARY_CURRENT_QUESTION_TYPE);
        currentPlayer = savedInstanceState.getInt(Constants.STATE_CURRENT_PLAYER);
        currentQuestion = savedInstanceState.getInt(Constants.STATE_CURRENT_QUESTION);
        answersTotal = savedInstanceState.getInt(Constants.STATE_ANSWERS_TOTAL);
        player1CorrectAnswers = savedInstanceState.getInt(Constants.STATE_PLAYER_1_CORRECT_ANSWERS);
        player2CorrectAnswers = savedInstanceState.getInt(Constants.STATE_PLAYER_2_CORRECT_ANSWERS);
        onCreateQuestionGenerated = savedInstanceState.getBoolean(Constants.STATE_ON_CREATE_QUESTION_GENERATED);

        //Set main heading text
        setMainHeadingText(true);
    }

    /**
     * Inflate questions/answers summary
     */
    private void inflateSummary() {
        //Set inflater & root view
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.answers_summary, null, false);

        //Question & answers text strings
        String currentQuestionText, currentQuestionAnswer1, currentQuestionAnswer2, currentQuestionAnswer3, currentQuestionAnswer4;

        //Set questions counter
        int questionCounter = 0;

        //Hashmaps keeping all selected answers for each player
        HashMap<String, String> allSelectedAnswersPlayer1 = allSelectedAnswers.get("player_1");
        HashMap<String, String> allSelectedAnswersPlayer2 = allSelectedAnswers.get("player_2");

        //'Winner Info' & 'Player Points' views
        TextView winnerInfo = rootView.findViewById(R.id.winner_info);
        TextView player1Points = rootView.findViewById(R.id.player_1_points);
        TextView player2Points = rootView.findViewById(R.id.player_2_points);

        //Views to keep questions indicator images
        indicatorQuestion1Player1 = rootView.findViewById(R.id.answer_indicator_1_player_1);
        indicatorQuestion1Player2 = rootView.findViewById(R.id.answer_indicator_1_player_2);
        indicatorQuestion2Player1 = rootView.findViewById(R.id.answer_indicator_2_player_1);
        indicatorQuestion2Player2 = rootView.findViewById(R.id.answer_indicator_2_player_2);
        indicatorQuestion3Player1 = rootView.findViewById(R.id.answer_indicator_3_player_1);
        indicatorQuestion3Player2 = rootView.findViewById(R.id.answer_indicator_3_player_2);
        indicatorQuestion4Player1 = rootView.findViewById(R.id.answer_indicator_4_player_1);
        indicatorQuestion4Player2 = rootView.findViewById(R.id.answer_indicator_4_player_2);
        indicatorQuestion5Player1 = rootView.findViewById(R.id.answer_indicator_5_player_1);
        indicatorQuestion5Player2 = rootView.findViewById(R.id.answer_indicator_5_player_2);

        //Used to build player answers strings
        String currentQuestionPlayerAnswers;

        //Keeping current correct answers
        ArrayList currentCorrectAnswers;

        //Loop through all used questions
        for (String questionNumber : alreadyUsedQuestions) {
            //Increase question counter
            questionCounter++;

            //Set current 'Answers Summary' question type
            //If one choice radio question
            if (Arrays.asList(singleChoiceRadioQuestions).contains(questionNumber)) {
                summaryCurrentQuestionType = Constants.QUESTION_ANSWER_TYPE_SINGLE_CHOICE_RADIO;
            }
            //If multi choice checkbox question
            else if (Arrays.asList(multipleChoiceCheckboxQuestions).contains(questionNumber)) {
                summaryCurrentQuestionType = Constants.QUESTION_ANSWER_TYPE_MULTIPLE_CHOICE_CHECKBOXES;
            }
            //If single answer text question
            else if (Arrays.asList(singleAnswerTextQuestions).contains(questionNumber)) {
                summaryCurrentQuestionType = Constants.QUESTION_ANSWER_TYPE_SINGLE_ANSWER_TEXT;
            }

            //Get texts for the current question & it's answers
            currentQuestionText = String.valueOf(questions.get(questionNumber).get(Constants.QUESTION_KEY_QUESTION));
            currentQuestionAnswer1 = String.valueOf(questions.get(questionNumber).get(Constants.QUESTION_KEY_ANSWER_1));
            currentQuestionAnswer2 = String.valueOf(questions.get(questionNumber).get(Constants.QUESTION_KEY_ANSWER_2));
            currentQuestionAnswer3 = String.valueOf(questions.get(questionNumber).get(Constants.QUESTION_KEY_ANSWER_3));
            currentQuestionAnswer4 = String.valueOf(questions.get(questionNumber).get(Constants.QUESTION_KEY_ANSWER_4));

            //Based on question counter
            switch (questionCounter) {
                case 1:
                    //Find related views
                    TextView questionExpandButton1 = rootView.findViewById(R.id.question_expand_button_1);
                    questionExpandButton1.setText(currentQuestionText);
                    answersSummary1 = rootView.findViewById(R.id.question_answers_summary_1);
                    TextView questionAllAnswers1 = rootView.findViewById(R.id.summary_all_answered_answers_1);

                    //Build correct answers string & set it as visible for the user
                    questionAllAnswers1.setText(buildCorrectAnswersString(questionNumber,
                            currentQuestionAnswer1, currentQuestionAnswer2, currentQuestionAnswer3, currentQuestionAnswer4));

                    //Get current correct answers array list
                    currentCorrectAnswers = answers.get(questionNumber);

                    //Prepare & show answers string for the Player 1
                    TextView questionAnswered1Player1 = rootView.findViewById(R.id.summary_answer_1_player_1);
                    currentQuestionPlayerAnswers = buildPlayerAnswersString("1", questionCounter, questionNumber, currentCorrectAnswers, allSelectedAnswersPlayer1.get(String.valueOf(questionCounter)));
                    questionAnswered1Player1.setText(currentQuestionPlayerAnswers);

                    //Prepare & show answers string for the Player 2
                    TextView questionAnswered1Player2 = rootView.findViewById(R.id.summary_answer_1_player_2);
                    currentQuestionPlayerAnswers = buildPlayerAnswersString("2", questionCounter, questionNumber, currentCorrectAnswers, allSelectedAnswersPlayer2.get(String.valueOf(questionCounter)));
                    questionAnswered1Player2.setText(currentQuestionPlayerAnswers);

                    //Set on click listener for the current question text view used as expand button 
                    questionExpandButton1.setOnClickListener(this);
                    break;
                case 2:
                    //Find related views
                    TextView questionExpandButton2 = rootView.findViewById(R.id.question_expand_button_2);
                    questionExpandButton2.setText(currentQuestionText);
                    answersSummary2 = rootView.findViewById(R.id.question_answers_summary_2);
                    TextView questionAllAnswers2 = rootView.findViewById(R.id.summary_all_answered_answers_2);

                    //Build correct answers string & set it as visible for the user
                    questionAllAnswers2.setText(buildCorrectAnswersString(questionNumber,
                            currentQuestionAnswer1, currentQuestionAnswer2, currentQuestionAnswer3, currentQuestionAnswer4));

                    //Get current correct answers array list
                    currentCorrectAnswers = answers.get(questionNumber);

                    //Prepare & show answers string for the Player 1
                    TextView questionAnswered2Player1 = rootView.findViewById(R.id.summary_answer_2_player_1);
                    currentQuestionPlayerAnswers = buildPlayerAnswersString("1", questionCounter, questionNumber, currentCorrectAnswers, allSelectedAnswersPlayer1.get(String.valueOf(questionCounter)));
                    questionAnswered2Player1.setText(currentQuestionPlayerAnswers);

                    //Prepare & show answers string for the Player 2
                    TextView questionAnswered2Player2 = rootView.findViewById(R.id.summary_answer_2_player_2);
                    currentQuestionPlayerAnswers = buildPlayerAnswersString("2", questionCounter, questionNumber, currentCorrectAnswers, allSelectedAnswersPlayer2.get(String.valueOf(questionCounter)));
                    questionAnswered2Player2.setText(currentQuestionPlayerAnswers);

                    //Set on click listener for the current question text view used as expand button 
                    questionExpandButton2.setOnClickListener(this);
                    break;
                case 3:
                    //Find related views
                    TextView questionExpandButton3 = rootView.findViewById(R.id.question_expand_button_3);
                    questionExpandButton3.setText(currentQuestionText);
                    answersSummary3 = rootView.findViewById(R.id.question_answers_summary_3);
                    TextView questionAllAnswers3 = rootView.findViewById(R.id.summary_all_answered_answers_3);

                    //Build correct answers string & set it as visible for the user
                    questionAllAnswers3.setText(buildCorrectAnswersString(questionNumber,
                            currentQuestionAnswer1, currentQuestionAnswer2, currentQuestionAnswer3, currentQuestionAnswer4));

                    //Get current correct answers array list
                    currentCorrectAnswers = answers.get(questionNumber);

                    //Prepare & show answers string for the Player 1
                    TextView questionAnswered3Player1 = rootView.findViewById(R.id.summary_answer_3_player_1);
                    currentQuestionPlayerAnswers = buildPlayerAnswersString("1", questionCounter, questionNumber, currentCorrectAnswers, allSelectedAnswersPlayer1.get(String.valueOf(questionCounter)));
                    questionAnswered3Player1.setText(currentQuestionPlayerAnswers);

                    //Prepare & show answers string for the Player 2
                    TextView questionAnswered3Player2 = rootView.findViewById(R.id.summary_answer_3_player_2);
                    currentQuestionPlayerAnswers = buildPlayerAnswersString("2", questionCounter, questionNumber, currentCorrectAnswers, allSelectedAnswersPlayer2.get(String.valueOf(questionCounter)));
                    questionAnswered3Player2.setText(currentQuestionPlayerAnswers);

                    //Set on click listener for the current question text view used as expand button 
                    questionExpandButton3.setOnClickListener(this);
                    break;
                case 4:
                    //Find related views
                    TextView questionExpandButton4 = rootView.findViewById(R.id.question_expand_button_4);
                    questionExpandButton4.setText(currentQuestionText);
                    answersSummary4 = rootView.findViewById(R.id.question_answers_summary_4);
                    TextView questionAllAnswers4 = rootView.findViewById(R.id.summary_all_answered_answers_4);

                    //Build correct answers string & set it as visible for the user
                    questionAllAnswers4.setText(buildCorrectAnswersString(questionNumber,
                            currentQuestionAnswer1, currentQuestionAnswer2, currentQuestionAnswer3, currentQuestionAnswer4));

                    //Get current correct answers array list
                    currentCorrectAnswers = answers.get(questionNumber);

                    //Prepare & show answers string for the Player 1
                    TextView questionAnswered4Player1 = rootView.findViewById(R.id.summary_answer_4_player_1);
                    currentQuestionPlayerAnswers = buildPlayerAnswersString("1", questionCounter, questionNumber, currentCorrectAnswers, allSelectedAnswersPlayer1.get(String.valueOf(questionCounter)));
                    questionAnswered4Player1.setText(currentQuestionPlayerAnswers);

                    //Prepare & show answers string for the Player 2
                    TextView questionAnswered4Player2 = rootView.findViewById(R.id.summary_answer_4_player_2);
                    currentQuestionPlayerAnswers = buildPlayerAnswersString("2", questionCounter, questionNumber, currentCorrectAnswers, allSelectedAnswersPlayer2.get(String.valueOf(questionCounter)));
                    questionAnswered4Player2.setText(currentQuestionPlayerAnswers);

                    //Set on click listener for the current question text view used as expand button 
                    questionExpandButton4.setOnClickListener(this);
                    break;
                case 5:
                    //Find related views
                    TextView questionExpandButton5 = rootView.findViewById(R.id.question_expand_button_5);
                    questionExpandButton5.setText(currentQuestionText);
                    answersSummary5 = rootView.findViewById(R.id.question_answers_summary_5);
                    TextView questionAllAnswers5 = rootView.findViewById(R.id.summary_all_answered_answers_5);

                    //Build correct answers string & set it as visible for the user
                    questionAllAnswers5.setText(buildCorrectAnswersString(questionNumber,
                            currentQuestionAnswer1, currentQuestionAnswer2, currentQuestionAnswer3, currentQuestionAnswer4));

                    //Get current correct answers array list
                    currentCorrectAnswers = answers.get(questionNumber);

                    //Prepare & show answers string for the Player 1
                    TextView questionAnswered5Player1 = rootView.findViewById(R.id.summary_answer_5_player_1);
                    currentQuestionPlayerAnswers = buildPlayerAnswersString("1", questionCounter, questionNumber, currentCorrectAnswers, allSelectedAnswersPlayer1.get(String.valueOf(questionCounter)));
                    questionAnswered5Player1.setText(currentQuestionPlayerAnswers);

                    //Prepare & show answers string for the Player 2
                    TextView questionAnswered5Player2 = rootView.findViewById(R.id.summary_answer_5_player_2);
                    currentQuestionPlayerAnswers = buildPlayerAnswersString("2", questionCounter, questionNumber, currentCorrectAnswers, allSelectedAnswersPlayer2.get(String.valueOf(questionCounter)));
                    questionAnswered5Player2.setText(currentQuestionPlayerAnswers);

                    //Set on click listener for the current question text view used as expand button 
                    questionExpandButton5.setOnClickListener(this);
                    break;
            }
        }

        //Prepare 'Winner Info' & the final Toast messages
        String finalToastMessage;
        String winnerInfoMessage;
        if (player1CorrectAnswers == player2CorrectAnswers) {
            winnerInfoMessage = getString(R.string.winner_info_is_tie);
            finalToastMessage = getString(R.string.final_toast_is_tie, player1CorrectAnswers);
        } else if (player1CorrectAnswers > player2CorrectAnswers) {
            winnerInfoMessage = getString(R.string.winner_info_no_tie, 1);
            finalToastMessage = getString(R.string.final_toast_no_tie, 1, player1CorrectAnswers, 2, player2CorrectAnswers);
        } else {
            winnerInfoMessage = getString(R.string.winner_info_no_tie, 2);
            finalToastMessage = getString(R.string.final_toast_no_tie, 2, player2CorrectAnswers, 1, player1CorrectAnswers);
        }
        finalToastMessage += " " + getString(R.string.final_toast_last_line);

        //Show 'Answers Summary' card view
        answersSummaryView.setVisibility(View.VISIBLE);

        //Set 'Winner Info' & 'Player Points' texts
        winnerInfo.setText(winnerInfoMessage);
        player1Points.setText(String.valueOf(player1CorrectAnswers));
        player2Points.setText(String.valueOf(player2CorrectAnswers));

        //Insert all into main view
        ViewGroup insertPoint = findViewById(R.id.answers_summary_container);
        insertPoint.addView(rootView,0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        //Make sure that question elements are not available until 'Reset' button is clicked
        disableQuestionElements();

        //If not restored instance state
        if(!restoredInstanceState) {
            showToast(finalToastMessage,true, FancyToast.SUCCESS);
        }
    }

    /**
     * Build the string with correct answers
     *
     * @param questionNumber         - question number [String]
     * @param currentQuestionAnswer1 - current question answer [String]
     * @param currentQuestionAnswer2 - current question answer [String]
     * @param currentQuestionAnswer3 - current question answer [String]
     * @param currentQuestionAnswer4 - current question answer [String]
     * @return - Correct answers string
     */
    private String buildCorrectAnswersString(String questionNumber,
                                             String currentQuestionAnswer1, String currentQuestionAnswer2, String currentQuestionAnswer3, String currentQuestionAnswer4) {
        //Prepare correct answers string
        StringBuilder oneStringCorrectAnswers = new StringBuilder();
        int answerCounter = 0;
        switch (summaryCurrentQuestionType) {
            case Constants.QUESTION_ANSWER_TYPE_SINGLE_CHOICE_RADIO:
            case Constants.QUESTION_ANSWER_TYPE_MULTIPLE_CHOICE_CHECKBOXES:
                ArrayList correctAnswersNumbers = answers.get(questionNumber);

                if (correctAnswersNumbers.contains(1)) {
                    oneStringCorrectAnswers.append(currentQuestionAnswer1);
                    answerCounter++;
                }
                if (correctAnswersNumbers.contains(2)) {
                    if (answerCounter > 0) {
                        oneStringCorrectAnswers.append(", ");
                    }
                    oneStringCorrectAnswers.append(currentQuestionAnswer2);
                    answerCounter++;
                }
                if (correctAnswersNumbers.contains(3)) {
                    if (answerCounter > 0) {
                        oneStringCorrectAnswers.append(", ");
                    }
                    oneStringCorrectAnswers.append(currentQuestionAnswer3);
                    answerCounter++;
                }
                if (correctAnswersNumbers.contains(4)) {
                    if (answerCounter > 0) {
                        oneStringCorrectAnswers.append(", ");
                    }
                    oneStringCorrectAnswers.append(currentQuestionAnswer4);
                }
                break;
            case Constants.QUESTION_ANSWER_TYPE_SINGLE_ANSWER_TEXT:
                ArrayList possibleAnswers = answers.get(questionNumber);
                for (Object possibleAnswer : possibleAnswers) {
                    answerCounter++;
                    if (answerCounter != 1) {
                        oneStringCorrectAnswers.append(", ");
                    }
                    oneStringCorrectAnswers.append(possibleAnswer.toString());
                }
                break;
        }

        //Return prepared string
        return oneStringCorrectAnswers.toString();
    }

    /**
     * Build the string with player answers
     *
     * @param playerNumber          - player number [String]
     * @param questionCounter       - question counter [int]
     * @param questionNumber        - question number [String]
     * @param currentCorrectAnswers - current correct answers [ArrayList]
     * @param playerAnswers         - player answers [String]
     * @return - Player answers string
     */
    private String buildPlayerAnswersString(String playerNumber, int questionCounter, String questionNumber, ArrayList currentCorrectAnswers, String playerAnswers) {
        //Flag to keep info if answer(s) is correct
        Boolean answerIsCorrect = false;

        //Prepare player answers string
        StringBuilder oneStringPlayerAnswers = new StringBuilder();
        switch (summaryCurrentQuestionType) {
            case Constants.QUESTION_ANSWER_TYPE_SINGLE_CHOICE_RADIO:
            case Constants.QUESTION_ANSWER_TYPE_MULTIPLE_CHOICE_CHECKBOXES:

                List<String> currentQuestionPlayerAnswers = Arrays.asList(playerAnswers.split("_"));
                int answerCounter = 0;
                for (Object answeredAnswer : currentQuestionPlayerAnswers) {
                    answerCounter++;
                    if (answerCounter != 1) {
                        oneStringPlayerAnswers.append(", ");
                    }

                    oneStringPlayerAnswers.append(String.valueOf(questions.get(questionNumber).get("answer_" + answeredAnswer)));
                }

                StringBuilder oneStringAnswers = new StringBuilder();
                answerCounter = 0;
                for (Object correctAnswer : currentCorrectAnswers) {
                    answerCounter++;
                    if (answerCounter != 1) {
                        oneStringAnswers.append("_");
                    }
                    oneStringAnswers.append(correctAnswer);
                }

                if (Objects.equals(oneStringAnswers.toString(), playerAnswers)) {
                    answerIsCorrect = true;
                }

                break;
            case Constants.QUESTION_ANSWER_TYPE_SINGLE_ANSWER_TEXT:
                oneStringPlayerAnswers.append(playerAnswers);
                for (Object correctAnswer : currentCorrectAnswers) {
                    if (Objects.equals(correctAnswer.toString().toLowerCase(), playerAnswers.toLowerCase())) {
                        answerIsCorrect = true;
                    }
                }
                break;
        }

        //If not restored instance state
        if(!restoredInstanceState) {
            //Count correct answers for each player
            if (answerIsCorrect) {
                if (Objects.equals(playerNumber, "1")) {
                    player1CorrectAnswers++;
                } else {
                    player2CorrectAnswers++;
                }
            }
        }

        //Indicator ID
        int indicatorId = getResources().getIdentifier(answerIsCorrect ? "indicator_correct_answer" : "indicator_wrong_answer", "drawable", getPackageName());

        //Show correct indicator image
        if (Objects.equals(playerNumber, "1")) {
            switch (questionCounter) {
                case 1:
                    loadGlideImage(context, indicatorId, indicatorQuestion1Player1);
                    break;
                case 2:
                    loadGlideImage(context, indicatorId, indicatorQuestion2Player1);
                    break;
                case 3:
                    loadGlideImage(context, indicatorId, indicatorQuestion3Player1);
                    break;
                case 4:
                    loadGlideImage(context, indicatorId, indicatorQuestion4Player1);
                    break;
                case 5:
                    loadGlideImage(context, indicatorId, indicatorQuestion5Player1);
                    break;
            }
        } else {
            switch (questionCounter) {
                case 1:
                    loadGlideImage(context, indicatorId, indicatorQuestion1Player2);
                    break;
                case 2:
                    loadGlideImage(context, indicatorId, indicatorQuestion2Player2);
                    break;
                case 3:
                    loadGlideImage(context, indicatorId, indicatorQuestion3Player2);
                    break;
                case 4:
                    loadGlideImage(context, indicatorId, indicatorQuestion4Player2);
                    break;
                case 5:
                    loadGlideImage(context, indicatorId, indicatorQuestion5Player2);
                    break;
            }
        }

        //Return prepared string
        return oneStringPlayerAnswers.toString();
    }

    /**
     * onClick method
     *
     * @param v - view [View]
     */
    @Override
    public void onClick(View v) {
        //Perform on click action
        switch (v.getId()) {
            case R.id.button_continue:
                //Confirm answer & generate the question for the next player
                confirmAndGenerateQuestion();
                break;
        }

        if (v.getId() == R.id.question_expand_button_1) {
            if (answersSummary1.isExpanded()) {
                answersSummary1.collapse();
            } else {
                collapseAllSummaryTabs();
                answersSummary1.expand();
            }
        } else if (v.getId() == R.id.question_expand_button_2) {
            if (answersSummary2.isExpanded()) {
                answersSummary2.collapse();
            } else {
                collapseAllSummaryTabs();
                answersSummary2.expand();
            }
        } else if (v.getId() == R.id.question_expand_button_3) {
            if (answersSummary3.isExpanded()) {
                answersSummary3.collapse();
            } else {
                collapseAllSummaryTabs();
                answersSummary3.expand();
            }
        } else if (v.getId() == R.id.question_expand_button_4) {
            if (answersSummary4.isExpanded()) {
                answersSummary4.collapse();
            } else {
                collapseAllSummaryTabs();
                answersSummary4.expand();
            }
        } else if (v.getId() == R.id.question_expand_button_5) {
            if (answersSummary5.isExpanded()) {
                answersSummary5.collapse();
            } else {
                collapseAllSummaryTabs();
                answersSummary5.expand();
            }
        }
    }

    /**
     * Collapse all 'Answers Summary' tabs
     */
    private void collapseAllSummaryTabs() {
        answersSummary1.collapse();
        answersSummary2.collapse();
        answersSummary3.collapse();
        answersSummary4.collapse();
        answersSummary5.collapse();
    }

    /**
     * Confirm answer & generate the question for the next player
     */
    private void confirmAndGenerateQuestion() {
        //If question has been answered
        if (questionAnswered()) {
            //Increment answers total
            answersTotal++;

            //Show summary if all question answered already
            if (answersTotal == Constants.MAX_ANSWERS) {
                //Hide 'Continue' button
                buttonContinue.setVisibility(View.INVISIBLE);

                //Inflate 'Answers Summary'
                inflateSummary();
            } else {
                //Generate new random question
                getRandomQuestion(false);

                //Show 'Next Player' view
                showNextPlayerView(false);
            }
        } else {
            //Show Toast when question is not answered yet
            showToast(getString(R.string.not_answered_question), false, FancyToast.WARNING);
        }
    }

    /**
     * Set questions and the corresponding answers (each answer has its index - indexes are starting from 1)
     */
    private void getRandomQuestion(boolean onCreate) {
        //Make sure that question is not generated multiple times on creation (in case if the first continue button was clicked very quickly)
        if (!onCreate || !onCreateQuestionGenerated) {
            //Make sure that there are still questions available & change question only when current player is 'Player 1'
            if (questionsToUse.size() > 0 && currentPlayer == 1) {
                //Increment current question counter
                currentQuestion++;

                //Generate random question index
                Random generator = new Random();
                int randomIndex = generator.nextInt(questionsToUse.size());

                //Get question number
                randomQuestionNumber = questionsToUse.get(randomIndex);

                //Add question into already used questions array list
                alreadyUsedQuestions.add(randomQuestionNumber);

                //Make sure that the same question can be used only once
                questionsToUse.remove(randomIndex);

                //Get question info
                getQuestionInfo();

                //Change 'stepper' step
                stepperIndicator.setCurrentStep(currentQuestion - 1);
            }

            //Clear answer elements values
            answerRadiosView.clearCheck();
            answer1CheckBoxView.setChecked(false);
            answer2CheckBoxView.setChecked(false);
            answer3CheckBoxView.setChecked(false);
            answer4CheckBoxView.setChecked(false);
            answerEditTextView.setText("");

            //Set main heading text
            setMainHeadingText(false);

            //Change player
            currentPlayer = currentPlayer == 1 ? 2 : 1;
        }

        //Update onCreateQuestionGenerated flag if question is generated on creation & restoredInstanceState is false
        if (onCreate && !restoredInstanceState) {
            onCreateQuestionGenerated = true;
        }
    }

    /**
     * Set main heading text
     *
     * @param switchPlayer - switch player flag [boolean]
     */
    private void setMainHeadingText(Boolean switchPlayer) {
        //Update main heading
        mainHeadingView.setText(getString(R.string.current_player, (switchPlayer ? (currentPlayer == 1 ? 2 : 1) : currentPlayer), currentQuestion));
    }

    /**
     * Get question info
     */
    private void getQuestionInfo() {
        //Set question text
        questionView.setText(String.valueOf(questions.get(randomQuestionNumber).get(Constants.QUESTION_KEY_QUESTION)));

        //Show question image
        int questionImageId = getResources().getIdentifier("question_" + randomQuestionNumber, "drawable", getPackageName());
        ImageView questionImageView = findViewById(R.id.question_image);
        loadGlideImage(context, questionImageId, questionImageView);

        //Handle answer elements visibility
        //If single choice radio question
        answerRadiosView.setVisibility(View.GONE);
        answerCheckboxesView.setVisibility(View.GONE);
        answerEditTextView.setVisibility(View.GONE);
        if (Arrays.asList(singleChoiceRadioQuestions).contains(randomQuestionNumber)) {
            quizLastQuestionType = Constants.QUESTION_ANSWER_TYPE_SINGLE_CHOICE_RADIO;

            answerRadiosView.setVisibility(View.VISIBLE);

            answer1RadioView.setText(String.valueOf(questions.get(randomQuestionNumber).get(Constants.QUESTION_KEY_ANSWER_1)));
            answer2RadioView.setText(String.valueOf(questions.get(randomQuestionNumber).get(Constants.QUESTION_KEY_ANSWER_2)));
            answer3RadioView.setText(String.valueOf(questions.get(randomQuestionNumber).get(Constants.QUESTION_KEY_ANSWER_3)));
            answer4RadioView.setText(String.valueOf(questions.get(randomQuestionNumber).get(Constants.QUESTION_KEY_ANSWER_4)));
        }
        //If multi choice checkbox question
        else if (Arrays.asList(multipleChoiceCheckboxQuestions).contains(randomQuestionNumber)) {
            quizLastQuestionType = Constants.QUESTION_ANSWER_TYPE_MULTIPLE_CHOICE_CHECKBOXES;

            answerCheckboxesView.setVisibility(View.VISIBLE);

            answer1CheckBoxView.setText(String.valueOf(questions.get(randomQuestionNumber).get(Constants.QUESTION_KEY_ANSWER_1)));
            answer2CheckBoxView.setText(String.valueOf(questions.get(randomQuestionNumber).get(Constants.QUESTION_KEY_ANSWER_2)));
            answer3CheckBoxView.setText(String.valueOf(questions.get(randomQuestionNumber).get(Constants.QUESTION_KEY_ANSWER_3)));
            answer4CheckBoxView.setText(String.valueOf(questions.get(randomQuestionNumber).get(Constants.QUESTION_KEY_ANSWER_4)));
        }
        //If single answer text question
        else if (Arrays.asList(singleAnswerTextQuestions).contains(randomQuestionNumber)) {
            quizLastQuestionType = Constants.QUESTION_ANSWER_TYPE_SINGLE_ANSWER_TEXT;

            answerEditTextView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Show toast
     *
     * @param text         - Toast text [String]
     * @param longDuration - if long duration [Boolean]
     * @param type         - Toast type [int]
     */
    private void showToast(String text, Boolean longDuration, int type) {
        int duration = longDuration ? FancyToast.LENGTH_LONG : FancyToast.LENGTH_SHORT;

        Toast toast = FancyToast.makeText(context, text, duration, type, false);
        toast.show();
    }

    /**
     * Check if question was answered or not & keep answers for later
     *
     * @return boolean if question was answered or not
     */
    private boolean questionAnswered() {
        //Player who answered last
        int playerWhoAnswered = currentPlayer == 1 ? 2 : 1;

        //Prepare hash maps
        HashMap<String, String> playerSelectedAnswers = new HashMap<>();
        HashMap<String, String> previousPlayerSelectedAnswers = allSelectedAnswers.get("player_" + playerWhoAnswered);

        //Set playerSelectedAnswers as previousPlayerSelectedAnswers
        if (previousPlayerSelectedAnswers != null && !previousPlayerSelectedAnswers.isEmpty()) {
            playerSelectedAnswers = previousPlayerSelectedAnswers;
        }

        //Check if question is answered & remember answers
        switch (quizLastQuestionType) {
            case Constants.QUESTION_ANSWER_TYPE_SINGLE_CHOICE_RADIO:
                int answerRadioButtonId = answerRadiosView.getCheckedRadioButtonId();
                if (answerRadioButtonId == -1) {
                    return false;
                } else {
                    RadioButton answerRadioButton = findViewById(answerRadioButtonId);
                    int answerId = Integer.parseInt(answerRadioButton.getTag().toString());

                    playerSelectedAnswers.put(String.valueOf(currentQuestion), String.valueOf(answerId));
                }
                break;
            case Constants.QUESTION_ANSWER_TYPE_MULTIPLE_CHOICE_CHECKBOXES:
                if (!answer1CheckBoxView.isChecked() && !answer2CheckBoxView.isChecked() && !answer3CheckBoxView.isChecked() && !answer4CheckBoxView.isChecked()) {
                    return false;
                } else {
                    ArrayList<String> selectedCheckboxAnswers = new ArrayList<>();
                    if (answer1CheckBoxView.isChecked()) {
                        selectedCheckboxAnswers.add("1");
                    }
                    if (answer2CheckBoxView.isChecked()) {
                        selectedCheckboxAnswers.add("2");
                    }
                    if (answer3CheckBoxView.isChecked()) {
                        selectedCheckboxAnswers.add("3");
                    }
                    if (answer4CheckBoxView.isChecked()) {
                        selectedCheckboxAnswers.add("4");
                    }
                    //Convert the ArrayList to String
                    StringBuilder oneStringAnswers = new StringBuilder();
                    int answerCounter = 0;
                    for (String answerId : selectedCheckboxAnswers) {
                        answerCounter++;
                        if (answerCounter != 1) {
                            oneStringAnswers.append("_");
                        }
                        oneStringAnswers.append(answerId);
                    }

                    playerSelectedAnswers.put(String.valueOf(currentQuestion), oneStringAnswers.toString());
                }
                break;
            case Constants.QUESTION_ANSWER_TYPE_SINGLE_ANSWER_TEXT:
                String answerText = answerEditTextView.getText().toString().trim();
                if (answerText.isEmpty()) {
                    return false;
                } else {
                    playerSelectedAnswers.put(String.valueOf(currentQuestion), answerText);
                }
                break;
        }

        //Keep answers for later
        allSelectedAnswers.put("player_" + playerWhoAnswered, playerSelectedAnswers);

        //Return true as answer was provided correctly
        return true;
    }

    /**
     * Show Next Player View
     */
    private void showNextPlayerView(boolean onCreate) {
        //Make sure that question elements are not available until 'Continue' button on 'Next Player' card view is clicked
        disableQuestionElements();

        //Hide 'Continue' button
        buttonContinue.setVisibility(View.GONE);

        //Show 'Next Player' card view
        nextPlayerView.setVisibility(View.VISIBLE);

        //Show message with the next player info
        String infoMessage;
        if (onCreate) {
            infoMessage = getString(R.string.next_player_info, 1);
        } else {
            infoMessage = getString(R.string.next_player_info, (currentPlayer == 1 ? 2 : 1));
        }
        nextPlayerInfoView.setText(infoMessage);
    }

    /**
     * Disable question elements
     */
    private void disableQuestionElements() {
        switch (quizLastQuestionType) {
            case Constants.QUESTION_ANSWER_TYPE_SINGLE_CHOICE_RADIO:
                answer1RadioView.setEnabled(false);
                answer2RadioView.setEnabled(false);
                answer3RadioView.setEnabled(false);
                answer4RadioView.setEnabled(false);
                break;
            case Constants.QUESTION_ANSWER_TYPE_MULTIPLE_CHOICE_CHECKBOXES:
                answer1CheckBoxView.setEnabled(false);
                answer2CheckBoxView.setEnabled(false);
                answer3CheckBoxView.setEnabled(false);
                answer4CheckBoxView.setEnabled(false);
                break;
            case Constants.QUESTION_ANSWER_TYPE_SINGLE_ANSWER_TEXT:
                answerEditTextView.setEnabled(false);
                answerEditTextView.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * Continue with the next player turn
     *
     * @param v - Triggering View
     */
    public void nextPlayerContinue(View v) {
        //Make sure that answers view is available
        switch (quizLastQuestionType) {
            case Constants.QUESTION_ANSWER_TYPE_SINGLE_CHOICE_RADIO:
                answer1RadioView.setEnabled(true);
                answer2RadioView.setEnabled(true);
                answer3RadioView.setEnabled(true);
                answer4RadioView.setEnabled(true);
                break;
            case Constants.QUESTION_ANSWER_TYPE_MULTIPLE_CHOICE_CHECKBOXES:
                answer1CheckBoxView.setEnabled(true);
                answer2CheckBoxView.setEnabled(true);
                answer3CheckBoxView.setEnabled(true);
                answer4CheckBoxView.setEnabled(true);
                break;
            case Constants.QUESTION_ANSWER_TYPE_SINGLE_ANSWER_TEXT:
                answerEditTextView.setEnabled(true);
                answerEditTextView.setVisibility(View.VISIBLE);
                answerEditTextView.setText("");
                break;
        }

        //Hide 'Continue' button
        buttonContinue.setVisibility(View.VISIBLE);

        //Hide 'Next Player' card view
        nextPlayerView.setVisibility(View.GONE);
    }

    public void resetQuiz(View v) {
        //Hide 'Answers Summary' card view
        answersSummaryView.setVisibility(View.GONE);

        //Reset data
        allSelectedAnswers = new HashMap<>();
        alreadyUsedQuestions = new ArrayList<>();
        questionsToUse = new ArrayList<>();
        currentPlayer = 1;
        currentQuestion = 0;
        answersTotal = 0;
        onCreateQuestionGenerated = false;
        quizLastQuestionType = null;
        summaryCurrentQuestionType = null;
        player1CorrectAnswers = 0;
        player2CorrectAnswers = 0;

        // Start StartScreenActivity
        Intent myIntent = new Intent(QuestionsActivity.this, StartScreenActivity.class);
        startActivity(myIntent);
        finish();
    }

    /**
     * Set questions and the corresponding answers (each answer has its index - indexes are starting from 1)
     */
    private void setQuestionsData() {
        //Declare hash map for values
        HashMap<String, String> value = new HashMap<>();

        //Single choice radio questions
        for (String questionIndex : singleChoiceRadioQuestions) {
            if (!Objects.equals(questionIndex, "1")) {
                value = new HashMap<>();
            }
            String question = "question_" + questionIndex;
            int questionId = getResources().getIdentifier(question, "string", getPackageName());
            value.put(Constants.QUESTION_KEY_QUESTION, getString(questionId));
            String questionAnswer = "question_" + questionIndex + "_answer_1";
            int questionAnswerId = getResources().getIdentifier(questionAnswer, "string", getPackageName());
            value.put(Constants.QUESTION_KEY_ANSWER_1, getString(questionAnswerId));
            questionAnswer = "question_" + questionIndex + "_answer_2";
            questionAnswerId = getResources().getIdentifier(questionAnswer, "string", getPackageName());
            value.put(Constants.QUESTION_KEY_ANSWER_2, getString(questionAnswerId));
            questionAnswer = "question_" + questionIndex + "_answer_3";
            questionAnswerId = getResources().getIdentifier(questionAnswer, "string", getPackageName());
            value.put(Constants.QUESTION_KEY_ANSWER_3, getString(questionAnswerId));
            questionAnswer = "question_" + questionIndex + "_answer_4";
            questionAnswerId = getResources().getIdentifier(questionAnswer, "string", getPackageName());
            value.put(Constants.QUESTION_KEY_ANSWER_4, getString(questionAnswerId));
            value.put(Constants.QUESTION_KEY_ANSWER_TYPE, Constants.QUESTION_ANSWER_TYPE_SINGLE_CHOICE_RADIO);
            questions.put(questionIndex, value);
        }

        //Multiple choice checkbox questions
        for (String questionIndex : multipleChoiceCheckboxQuestions) {
            value = new HashMap<>();
            String question = "question_" + questionIndex;
            int questionId = getResources().getIdentifier(question, "string", getPackageName());
            value.put(Constants.QUESTION_KEY_QUESTION, getString(questionId));
            String questionAnswer = "question_" + questionIndex + "_answer_1";
            int questionAnswerId = getResources().getIdentifier(questionAnswer, "string", getPackageName());
            value.put(Constants.QUESTION_KEY_ANSWER_1, getString(questionAnswerId));
            questionAnswer = "question_" + questionIndex + "_answer_2";
            questionAnswerId = getResources().getIdentifier(questionAnswer, "string", getPackageName());
            value.put(Constants.QUESTION_KEY_ANSWER_2, getString(questionAnswerId));
            questionAnswer = "question_" + questionIndex + "_answer_3";
            questionAnswerId = getResources().getIdentifier(questionAnswer, "string", getPackageName());
            value.put(Constants.QUESTION_KEY_ANSWER_3, getString(questionAnswerId));
            questionAnswer = "question_" + questionIndex + "_answer_4";
            questionAnswerId = getResources().getIdentifier(questionAnswer, "string", getPackageName());
            value.put(Constants.QUESTION_KEY_ANSWER_4, getString(questionAnswerId));
            value.put(Constants.QUESTION_KEY_ANSWER_TYPE, Constants.QUESTION_ANSWER_TYPE_MULTIPLE_CHOICE_CHECKBOXES);
            questions.put(questionIndex, value);
        }

        //Single answer text questions
        for (String questionIndex : singleAnswerTextQuestions) {
            value = new HashMap<>();
            String question = "question_" + questionIndex;
            int questionId = getResources().getIdentifier(question, "string", getPackageName());
            value.put(Constants.QUESTION_KEY_QUESTION, getString(questionId));
            value.put(Constants.QUESTION_KEY_ANSWER_TYPE, Constants.QUESTION_ANSWER_TYPE_SINGLE_ANSWER_TEXT);
            questions.put(questionIndex, value);
        }

        //Answer indexes for Question 1
        ArrayList correctAnswer = new ArrayList();
        correctAnswer.add(3);
        answers.put("1", correctAnswer);

        //Answer indexes for Question 2
        correctAnswer = new ArrayList();
        correctAnswer.add(3);
        answers.put("2", correctAnswer);

        //Answer indexes for Question 3
        correctAnswer = new ArrayList();
        correctAnswer.add(1);
        answers.put("3", correctAnswer);

        //Answer indexes for Question 4
        correctAnswer = new ArrayList();
        correctAnswer.add(1);
        answers.put("4", correctAnswer);

        //Answer indexes for Question 5
        correctAnswer = new ArrayList();
        correctAnswer.add(4);
        answers.put("5", correctAnswer);

        //Answer indexes for Question 6
        correctAnswer = new ArrayList();
        correctAnswer.add(2);
        answers.put("6", correctAnswer);

        //Answer indexes for Question 7
        correctAnswer = new ArrayList();
        correctAnswer.add(2);
        answers.put("7", correctAnswer);

        //Answer indexes for Question 8
        correctAnswer = new ArrayList();
        correctAnswer.add(2);
        answers.put("8", correctAnswer);

        //Answer indexes for Question 9
        correctAnswer = new ArrayList();
        correctAnswer.add(3);
        answers.put("9", correctAnswer);

        //Answer indexes for Question 10
        correctAnswer = new ArrayList();
        correctAnswer.add(3);
        answers.put("10", correctAnswer);

        //Answer indexes for Question 11
        correctAnswer = new ArrayList();
        correctAnswer.add(1);
        correctAnswer.add(2);
        correctAnswer.add(4);
        answers.put("11", correctAnswer);

        //Answer indexes for Question 12
        correctAnswer = new ArrayList();
        correctAnswer.add(getString(R.string.question_12_answer_1));
        correctAnswer.add(getString(R.string.question_12_answer_1_alt));
        answers.put("12", correctAnswer);

        //Answer indexes for Question 13
        correctAnswer = new ArrayList();
        correctAnswer.add(3);
        answers.put("13", correctAnswer);

        //Answer indexes for Question 14
        correctAnswer = new ArrayList();
        correctAnswer.add(getString(R.string.question_14_answer_1));
        answers.put("14", correctAnswer);

        //Answer indexes for Question 15
        correctAnswer = new ArrayList();
        correctAnswer.add(1);
        correctAnswer.add(4);
        answers.put("15", correctAnswer);

        //Answer indexes for Question 16
        correctAnswer = new ArrayList();
        correctAnswer.add(1);
        answers.put("16", correctAnswer);

        //Answer indexes for Question 17
        correctAnswer = new ArrayList();
        correctAnswer.add(3);
        answers.put("17", correctAnswer);

        //Answer indexes for Question 18
        correctAnswer = new ArrayList();
        correctAnswer.add(1);
        correctAnswer.add(2);
        correctAnswer.add(3);
        answers.put("18", correctAnswer);

        //Answer indexes for Question 19
        correctAnswer = new ArrayList();
        correctAnswer.add(getString(R.string.question_19_answer_1));
        correctAnswer.add(getString(R.string.question_19_answer_1_alt));
        answers.put("19", correctAnswer);

        //Answer indexes for Question 20
        correctAnswer = new ArrayList();
        correctAnswer.add(3);
        answers.put("20", correctAnswer);

        //Answer indexes for Question 21
        correctAnswer = new ArrayList();
        correctAnswer.add(getString(R.string.question_21_answer_1));
        answers.put("21", correctAnswer);

        //Answer indexes for Question 22
        correctAnswer = new ArrayList();
        correctAnswer.add(1);
        correctAnswer.add(2);
        correctAnswer.add(3);
        answers.put("22", correctAnswer);

        //Answer indexes for Question 23
        correctAnswer = new ArrayList();
        correctAnswer.add(1);
        answers.put("23", correctAnswer);

        //Answer indexes for Question 24
        correctAnswer = new ArrayList();
        correctAnswer.add(1);
        correctAnswer.add(2);
        correctAnswer.add(4);
        answers.put("24", correctAnswer);

        //Answer indexes for Question 25
        correctAnswer = new ArrayList();
        correctAnswer.add(1);
        answers.put("25", correctAnswer);

        //Answer indexes for Question 26
        correctAnswer = new ArrayList();
        correctAnswer.add(2);
        answers.put("26", correctAnswer);

        //Answer indexes for Question 27
        correctAnswer = new ArrayList();
        correctAnswer.add(getString(R.string.question_27_answer_1));
        answers.put("27", correctAnswer);

        //Answer indexes for Question 28
        correctAnswer = new ArrayList();
        correctAnswer.add(3);
        answers.put("28", correctAnswer);

        //Answer indexes for Question 29
        correctAnswer = new ArrayList();
        correctAnswer.add(1);
        correctAnswer.add(3);
        correctAnswer.add(4);
        answers.put("29", correctAnswer);

        //Answer indexes for Question 30
        correctAnswer = new ArrayList();
        correctAnswer.add(2);
        answers.put("30", correctAnswer);

        //Answer indexes for Question 31
        correctAnswer = new ArrayList();
        correctAnswer.add(2);
        answers.put("31", correctAnswer);

        //Answer indexes for Question 32
        correctAnswer = new ArrayList();
        correctAnswer.add(getString(R.string.question_32_answer_1));
        answers.put("32", correctAnswer);

        //Answer indexes for Question 33
        correctAnswer = new ArrayList();
        correctAnswer.add(3);
        answers.put("33", correctAnswer);

        //Answer indexes for Question 34
        correctAnswer = new ArrayList();
        correctAnswer.add(1);
        correctAnswer.add(2);
        correctAnswer.add(4);
        answers.put("34", correctAnswer);

        //Answer indexes for Question 35
        correctAnswer = new ArrayList();
        correctAnswer.add(1);
        answers.put("35", correctAnswer);

        //Answer indexes for Question 36
        correctAnswer = new ArrayList();
        correctAnswer.add(2);
        answers.put("36", correctAnswer);

        //Answer indexes for Question 37
        correctAnswer = new ArrayList();
        correctAnswer.add(1);
        answers.put("37", correctAnswer);

        //Answer indexes for Question 38
        correctAnswer = new ArrayList();
        correctAnswer.add(getString(R.string.question_38_answer_1));
        answers.put("38", correctAnswer);

        //Answer indexes for Question 39
        correctAnswer = new ArrayList();
        correctAnswer.add(1);
        answers.put("39", correctAnswer);

        //Answer indexes for Question 40
        correctAnswer = new ArrayList();
        correctAnswer.add(getString(R.string.question_40_answer_1));
        answers.put("40", correctAnswer);

        //Answer indexes for Question 41
        correctAnswer = new ArrayList();
        correctAnswer.add(2);
        answers.put("41", correctAnswer);

        //Answer indexes for Question 42
        correctAnswer = new ArrayList();
        correctAnswer.add(getString(R.string.question_42_answer_1));
        answers.put("42", correctAnswer);

        //Answer indexes for Question 43
        correctAnswer = new ArrayList();
        correctAnswer.add(2);
        answers.put("43", correctAnswer);

        //Answer indexes for Question 44
        correctAnswer = new ArrayList();
        correctAnswer.add(1);
        correctAnswer.add(2);
        correctAnswer.add(4);
        answers.put("44", correctAnswer);

        //Answer indexes for Question 45
        correctAnswer = new ArrayList();
        correctAnswer.add(getString(R.string.question_45_answer_1));
        answers.put("45", correctAnswer);

        //Answer indexes for Question 46
        correctAnswer = new ArrayList();
        correctAnswer.add(2);
        answers.put("46", correctAnswer);

        //Answer indexes for Question 47
        correctAnswer = new ArrayList();
        correctAnswer.add(4);
        answers.put("47", correctAnswer);

        //Answer indexes for Question 48
        correctAnswer = new ArrayList();
        correctAnswer.add(2);
        answers.put("48", correctAnswer);

        //Answer indexes for Question 49
        correctAnswer = new ArrayList();
        correctAnswer.add(3);
        answers.put("49", correctAnswer);

        //Answer indexes for Question 50
        correctAnswer = new ArrayList();
        correctAnswer.add(1);
        answers.put("50", correctAnswer);

        //Answer indexes for Question 51
        correctAnswer = new ArrayList();
        correctAnswer.add(getString(R.string.question_51_answer_1));
        correctAnswer.add(getString(R.string.question_51_answer_1_alt));
        answers.put("51", correctAnswer);

        //Answer indexes for Question 52
        correctAnswer = new ArrayList();
        correctAnswer.add(2);
        answers.put("52", correctAnswer);

        //Answer indexes for Question 53
        correctAnswer = new ArrayList();
        correctAnswer.add(getString(R.string.question_53_answer_1));
        correctAnswer.add(getString(R.string.question_53_answer_1_alt));
        correctAnswer.add(getString(R.string.question_53_answer_1_alt_2));
        answers.put("53", correctAnswer);

        //Answer indexes for Question 54
        correctAnswer = new ArrayList();
        correctAnswer.add(1);
        correctAnswer.add(2);
        correctAnswer.add(3);
        answers.put("54", correctAnswer);

        //Answer indexes for Question 55
        correctAnswer = new ArrayList();
        correctAnswer.add(1);
        correctAnswer.add(3);
        correctAnswer.add(4);
        answers.put("55", correctAnswer);
    }
}