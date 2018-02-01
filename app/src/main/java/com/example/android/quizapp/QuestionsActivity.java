package com.example.android.quizapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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

public class QuestionsActivity extends AppCompatActivity implements OnClickListener {

    //Current context
    private Context context;

    //Hashmaps for keeping questions & answers
    private final HashMap<String, HashMap<String, String>> questions = new HashMap<>();
    private final HashMap<String, ArrayList> answers = new HashMap<>();
    private HashMap<String, HashMap<String, String>> allSelectedAnswers = new HashMap<>();
    ArrayList<String> alreadyUsedQuestions = new ArrayList<String>();

    //Question numbers per each answer type
    String[] singleChoiceRadioQuestions = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "13", "17", "20", "23",
            "25", "26", "28", "30", "31", "33", "35", "36", "37", "39", "41", "43", "46",
            "47", "48", "49", "50", "52"};
    String[] multipleChoiceCheckboxQuestions = {"11", "15", "16", "18", "22", "24", "29", "34", "44", "54", "55"};
    String[] singleAnswerTextQuestions = {"12", "14", "19", "21", "27", "32", "38", "40", "42", "45", "51", "53"};

    //Total questions count
    int questionsCount = 55;

    //Array list to keep all questions indexes (it will be used later to generate random questions)
    ArrayList<String> questionsToUse = new ArrayList<String>();

    //Last used question types for the 'Quiz' & 'Answers Summary'
    private String quizLastQuestionType;
    private String summaryCurrentQuestionType;

    //Additional counters
    int currentPlayer = 1;
    int currentQuestion = 0;
    int answersTotal = 0;
    int player1CorrectAnswers = 0, player2CorrectAnswers = 0;

    //Flag to keep info if current random question was generated in onCreate method
    private boolean onCreateQuestionGenerated = false;

    //Main heading & continue button views
    private TextView MainHeadingView;
    private ImageView buttonContinue;

    //Questions & Answer views
    private TextView QuestionView;
    private RadioGroup AnswerRadiosView;
    private View AnswerCheckboxesView;
    private EditText AnswerEditTextView;
    private RadioButton Answer1RadioView, Answer2RadioView, Answer3RadioView, Answer4RadioView;
    private CheckBox Answer1CheckBoxView, Answer2CheckBoxView, Answer3CheckBoxView, Answer4CheckBoxView;

    //'Next Player' card view
    private CardView nextPlayerView;
    private TextView nextPlayerInfoView;

    //'Answers Summary' card view
    private CardView answersSummaryView;
    private TextView winnerInfo;
    private TextView player1Points, player2Points;
    private ExpandableLayout answersSummary1, answersSummary2, answersSummary3, answersSummary4, answersSummary5;
    private TextView questionExpandButton1, questionExpandButton2, questionExpandButton3, questionExpandButton4, questionExpandButton5;
    private TextView questionAllAnswers1, questionAllAnswers2, questionAllAnswers3, questionAllAnswers4, questionAllAnswers5;
    private TextView questionAnswered1Player1, questionAnswered1Player2, questionAnswered2Player1, questionAnswered2Player2,
                     questionAnswered3Player1, questionAnswered3Player2, questionAnswered4Player1, questionAnswered4Player2,
                     questionAnswered5Player1, questionAnswered5Player2;
    private ImageView indicatorQuestion1Player1, indicatorQuestion1Player2, indicatorQuestion2Player1, indicatorQuestion2Player2,
                      indicatorQuestion3Player1, indicatorQuestion3Player2, indicatorQuestion4Player1, indicatorQuestion4Player2,
                      indicatorQuestion5Player1, indicatorQuestion5Player2;

    //Inflater used by 'Answers Summary' card view
    private LayoutInflater inflater;

    //Stepper indicator
    private StepperIndicator stepperIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Call super class implementation of onCreate method
        super.onCreate(savedInstanceState);

        //Hide action bar
        hideActionBar();

        //Set 'activity_questions' as a main design file
        setContentView(R.layout.activity_questions);

        //Set application context
        context = getApplicationContext();

        //Main heading
        MainHeadingView = findViewById(R.id.main_heading);

        //Stepper indicator
        stepperIndicator = findViewById(R.id.stepper_indicator);

        //Question & answer views
        QuestionView = findViewById(R.id.question);
        AnswerRadiosView = findViewById(R.id.answers_r);
        AnswerCheckboxesView = findViewById(R.id.answers_c);
        AnswerEditTextView = findViewById(R.id.answer_e);
        Answer1RadioView = findViewById(R.id.answer_1_r);
        Answer2RadioView = findViewById(R.id.answer_2_r);
        Answer3RadioView = findViewById(R.id.answer_3_r);
        Answer4RadioView = findViewById(R.id.answer_4_r);
        Answer1CheckBoxView = findViewById(R.id.answer_1_c);
        Answer2CheckBoxView = findViewById(R.id.answer_2_c);
        Answer3CheckBoxView = findViewById(R.id.answer_3_c);
        Answer4CheckBoxView = findViewById(R.id.answer_4_c);

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
        for(int i = 1; i <= questionsCount; i++) {
            questionsToUse.add(String.valueOf(i));
        }

        //'Continue' button image
        buttonContinue = findViewById(R.id.button_continue);

        //Set on click listener event for button
        buttonContinue.setOnClickListener(this);

        //Set questions and the corresponding answers
        setQuestionsData();

        //Get random question
        getRandomQuestion(true);

        //Show 'Next Player' view before first question
        showNextPlayerView(true);
    }

    /**
     * Inflate questions/answers summary
     */
    private void inflateSummary()
    {
        //Set inflater & root view
        inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.answers_summary, null, false);

        //Question & answers text strings
        String currentQuestionText, currentQuestionAnswer1, currentQuestionAnswer2, currentQuestionAnswer3, currentQuestionAnswer4;

        //Set questions counter
        int questionCounter = 0;

        //Hashmaps keeping all selected answers for each player
        HashMap<String, String> allSelectedAnswersPlayer1 = allSelectedAnswers.get("player_1");
        HashMap<String, String> allSelectedAnswersPlayer2 = allSelectedAnswers.get("player_2");

        //'Winner Info' & 'Player Points' views
        winnerInfo = rootView.findViewById(R.id.winner_info);
        player1Points = rootView.findViewById(R.id.player_1_points);
        player2Points = rootView.findViewById(R.id.player_2_points);

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
        for (String questionNumber: alreadyUsedQuestions) {
            //Increase question counter
            questionCounter++;

            //Set current 'Answers Summary' question type
            //If one choice radio question
            if (Arrays.asList(singleChoiceRadioQuestions).contains(questionNumber)) {
                summaryCurrentQuestionType = Constants.QUESTION_ANSWER_TYPE_SINGLE_CHOICE_RADIO;
            }
            //If multi choice checkbox question
            else if (Arrays.asList(multipleChoiceCheckboxQuestions).contains(questionNumber)) {
                summaryCurrentQuestionType = Constants.QUESTION_ANSWER_TYPE_MULIPLE_CHOICE_CHECKBOXES;
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
                    questionExpandButton1 = rootView.findViewById(R.id.question_expand_button_1);
                    questionExpandButton1.setText(currentQuestionText);
                    answersSummary1 = rootView.findViewById(R.id.question_answers_summary_1);
                    questionAllAnswers1 = rootView.findViewById(R.id.summary_all_answered_answers_1);

                    //Build correct answers string & set it as visible for the user
                    questionAllAnswers1.setText(buildCorrectAnswersString(questionNumber,
                                                                          currentQuestionAnswer1, currentQuestionAnswer2, currentQuestionAnswer3, currentQuestionAnswer4));
                    
                    //Get current correct answers array list
                    currentCorrectAnswers = answers.get(questionNumber);

                    //Prepare & show answers string for the Player 1
                    questionAnswered1Player1 = rootView.findViewById(R.id.summary_answer_1_player_1);
                    currentQuestionPlayerAnswers = buildPlayerAnswersString("1", questionCounter, questionNumber, currentCorrectAnswers, allSelectedAnswersPlayer1.get(String.valueOf(questionCounter)));
                    questionAnswered1Player1.setText(currentQuestionPlayerAnswers);

                    //Prepare & show answers string for the Player 2
                    questionAnswered1Player2 = rootView.findViewById(R.id.summary_answer_1_player_2);
                    currentQuestionPlayerAnswers = buildPlayerAnswersString("2", questionCounter, questionNumber, currentCorrectAnswers, allSelectedAnswersPlayer2.get(String.valueOf(questionCounter)));
                    questionAnswered1Player2.setText(currentQuestionPlayerAnswers);

                    //Set on click listener for the current question text view used as expand button 
                    questionExpandButton1.setOnClickListener(this);
                    break;
                case 2:
                    //Find related views
                    questionExpandButton2 = rootView.findViewById(R.id.question_expand_button_2);
                    questionExpandButton2.setText(currentQuestionText);
                    answersSummary2 = rootView.findViewById(R.id.question_answers_summary_2);
                    questionAllAnswers2 = rootView.findViewById(R.id.summary_all_answered_answers_2);

                    //Build correct answers string & set it as visible for the user
                    questionAllAnswers2.setText(buildCorrectAnswersString(questionNumber,
                                                                          currentQuestionAnswer1, currentQuestionAnswer2, currentQuestionAnswer3, currentQuestionAnswer4));

                    //Get current correct answers array list
                    currentCorrectAnswers = answers.get(questionNumber);

                    //Prepare & show answers string for the Player 1
                    questionAnswered2Player1 = rootView.findViewById(R.id.summary_answer_2_player_1);
                    currentQuestionPlayerAnswers = buildPlayerAnswersString("1", questionCounter, questionNumber, currentCorrectAnswers, allSelectedAnswersPlayer1.get(String.valueOf(questionCounter)));
                    questionAnswered2Player1.setText(currentQuestionPlayerAnswers);

                    //Prepare & show answers string for the Player 2
                    questionAnswered2Player2 = rootView.findViewById(R.id.summary_answer_2_player_2);
                    currentQuestionPlayerAnswers = buildPlayerAnswersString("2", questionCounter, questionNumber, currentCorrectAnswers, allSelectedAnswersPlayer2.get(String.valueOf(questionCounter)));
                    questionAnswered2Player2.setText(currentQuestionPlayerAnswers);

                    //Set on click listener for the current question text view used as expand button 
                    questionExpandButton2.setOnClickListener(this);
                    break;
                case 3:
                    //Find related views
                    questionExpandButton3 = rootView.findViewById(R.id.question_expand_button_3);
                    questionExpandButton3.setText(currentQuestionText);
                    answersSummary3 = rootView.findViewById(R.id.question_answers_summary_3);
                    questionAllAnswers3 = rootView.findViewById(R.id.summary_all_answered_answers_3);

                    //Build correct answers string & set it as visible for the user
                    questionAllAnswers3.setText(buildCorrectAnswersString(questionNumber,
                                                                          currentQuestionAnswer1, currentQuestionAnswer2, currentQuestionAnswer3, currentQuestionAnswer4));

                    //Get current correct answers array list
                    currentCorrectAnswers = answers.get(questionNumber);

                    //Prepare & show answers string for the Player 1
                    questionAnswered3Player1 = rootView.findViewById(R.id.summary_answer_3_player_1);
                    currentQuestionPlayerAnswers = buildPlayerAnswersString("1", questionCounter, questionNumber, currentCorrectAnswers, allSelectedAnswersPlayer1.get(String.valueOf(questionCounter)));
                    questionAnswered3Player1.setText(currentQuestionPlayerAnswers);

                    //Prepare & show answers string for the Player 2
                    questionAnswered3Player2 = rootView.findViewById(R.id.summary_answer_3_player_2);
                    currentQuestionPlayerAnswers = buildPlayerAnswersString("2", questionCounter, questionNumber, currentCorrectAnswers, allSelectedAnswersPlayer2.get(String.valueOf(questionCounter)));
                    questionAnswered3Player2.setText(currentQuestionPlayerAnswers);

                    //Set on click listener for the current question text view used as expand button 
                    questionExpandButton3.setOnClickListener(this);
                    break;
                case 4:
                    //Find related views
                    questionExpandButton4 = rootView.findViewById(R.id.question_expand_button_4);
                    questionExpandButton4.setText(currentQuestionText);
                    answersSummary4 = rootView.findViewById(R.id.question_answers_summary_4);
                    questionAllAnswers4 = rootView.findViewById(R.id.summary_all_answered_answers_4);

                    //Build correct answers string & set it as visible for the user
                    questionAllAnswers4.setText(buildCorrectAnswersString(questionNumber,
                                                                          currentQuestionAnswer1, currentQuestionAnswer2, currentQuestionAnswer3, currentQuestionAnswer4));

                    //Get current correct answers array list
                    currentCorrectAnswers = answers.get(questionNumber);

                    //Prepare & show answers string for the Player 1
                    questionAnswered4Player1 = rootView.findViewById(R.id.summary_answer_4_player_1);
                    currentQuestionPlayerAnswers = buildPlayerAnswersString("1", questionCounter, questionNumber, currentCorrectAnswers, allSelectedAnswersPlayer1.get(String.valueOf(questionCounter)));
                    questionAnswered4Player1.setText(currentQuestionPlayerAnswers);

                    //Prepare & show answers string for the Player 2
                    questionAnswered4Player2 = rootView.findViewById(R.id.summary_answer_4_player_2);
                    currentQuestionPlayerAnswers = buildPlayerAnswersString("2", questionCounter, questionNumber, currentCorrectAnswers, allSelectedAnswersPlayer2.get(String.valueOf(questionCounter)));
                    questionAnswered4Player2.setText(currentQuestionPlayerAnswers);

                    //Set on click listener for the current question text view used as expand button 
                    questionExpandButton4.setOnClickListener(this);
                    break;
                case 5:
                    //Find related views
                    questionExpandButton5 = rootView.findViewById(R.id.question_expand_button_5);
                    questionExpandButton5.setText(currentQuestionText);
                    answersSummary5 = rootView.findViewById(R.id.question_answers_summary_5);
                    questionAllAnswers5 = rootView.findViewById(R.id.summary_all_answered_answers_5);

                    //Build correct answers string & set it as visible for the user
                    questionAllAnswers5.setText(buildCorrectAnswersString(questionNumber,
                                                                          currentQuestionAnswer1, currentQuestionAnswer2, currentQuestionAnswer3, currentQuestionAnswer4));

                    //Get current correct answers array list
                    currentCorrectAnswers = answers.get(questionNumber);

                    //Prepare & show answers string for the Player 1
                    questionAnswered5Player1 = rootView.findViewById(R.id.summary_answer_5_player_1);
                    currentQuestionPlayerAnswers = buildPlayerAnswersString("1", questionCounter, questionNumber, currentCorrectAnswers, allSelectedAnswersPlayer1.get(String.valueOf(questionCounter)));
                    questionAnswered5Player1.setText(currentQuestionPlayerAnswers);

                    //Prepare & show answers string for the Player 2
                    questionAnswered5Player2 = rootView.findViewById(R.id.summary_answer_5_player_2);
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
        if(player1CorrectAnswers == player2CorrectAnswers)
        {
            winnerInfoMessage = getString(R.string.winner_info_is_tie);
            finalToastMessage = getString(R.string.final_toast_is_tie, player1CorrectAnswers);
        }
        else if(player1CorrectAnswers > player2CorrectAnswers)
        {
            winnerInfoMessage = getString(R.string.winner_info_no_tie, 1);
            finalToastMessage = getString(R.string.final_toast_no_tie, 1, player1CorrectAnswers, 2, player2CorrectAnswers);
        }
        else
        {
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
        insertPoint.addView(rootView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        //Make sure that question elements are not available until 'Reset' button is clicked
        disableQuestionElements();

        //Show final Toast message
        showToast(finalToastMessage,true, FancyToast.SUCCESS);
    }

    /**
     * Build the string with correct answers
     *
     * @param questionNumber
     * @param currentQuestionAnswer1
     * @param currentQuestionAnswer2
     * @param currentQuestionAnswer3
     * @param currentQuestionAnswer4
     *
     * @return - Correct answers string
     */
    private String buildCorrectAnswersString(String questionNumber,
                                             String currentQuestionAnswer1, String currentQuestionAnswer2, String currentQuestionAnswer3, String currentQuestionAnswer4)
    {
        //Prepare correct answers string
        StringBuilder oneStringCorrectAnswers = new StringBuilder();
        int answerCounter = 0;
        switch (summaryCurrentQuestionType) {
            case Constants.QUESTION_ANSWER_TYPE_SINGLE_CHOICE_RADIO:
            case Constants.QUESTION_ANSWER_TYPE_MULIPLE_CHOICE_CHECKBOXES:
                ArrayList correctAnswersNumbers = answers.get(questionNumber);

                if (correctAnswersNumbers.contains(1)) {
                    oneStringCorrectAnswers.append(currentQuestionAnswer1);
                    answerCounter++;
                }
                if (correctAnswersNumbers.contains(2)) {
                    if(answerCounter > 0)
                    {
                        oneStringCorrectAnswers.append(", ");
                    }
                    oneStringCorrectAnswers.append(currentQuestionAnswer2);
                    answerCounter++;
                }
                if (correctAnswersNumbers.contains(3)) {
                    if(answerCounter > 0)
                    {
                        oneStringCorrectAnswers.append(", ");
                    }
                    oneStringCorrectAnswers.append(currentQuestionAnswer3);
                    answerCounter++;
                }
                if (correctAnswersNumbers.contains(4)) {
                    if(answerCounter > 0)
                    {
                        oneStringCorrectAnswers.append(", ");
                    }
                    oneStringCorrectAnswers.append(currentQuestionAnswer4);
                }
                break;
            case Constants.QUESTION_ANSWER_TYPE_SINGLE_ANSWER_TEXT:
                ArrayList possibleAnswers = answers.get(questionNumber);
                for(Object possibleAnswer : possibleAnswers) {
                    answerCounter++;
                    if(answerCounter != 1) {
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
     * @param playerNumber
     * @param questionCounter
     * @param questionNumber
     * @param currentCorrectAnswers
     * @param playerAnswers
     *
     * @return - Player answers string
     */
    private String buildPlayerAnswersString(String playerNumber, int questionCounter, String questionNumber, ArrayList currentCorrectAnswers, String playerAnswers)
    {
        //Flag to keep info if answer(s) is correct
        Boolean answerIsCorrect = false;

        //Prepare player answers string
        StringBuilder oneStringPlayerAnswers = new StringBuilder();
        switch (summaryCurrentQuestionType) {
            case Constants.QUESTION_ANSWER_TYPE_SINGLE_CHOICE_RADIO:
            case Constants.QUESTION_ANSWER_TYPE_MULIPLE_CHOICE_CHECKBOXES:

                List<String> currentQuestionPlayerAnswers = Arrays.asList(playerAnswers.split("_"));
                int answerCounter = 0;
                for(Object answeredAnswer : currentQuestionPlayerAnswers) {
                    answerCounter++;
                    if(answerCounter != 1) {
                        oneStringPlayerAnswers.append(", ");
                    }

                    oneStringPlayerAnswers.append(String.valueOf(questions.get(questionNumber).get("answer_"+answeredAnswer)));
                }

                StringBuilder oneStringAnswers = new StringBuilder();
                answerCounter = 0;
                for(Object correctAnswer : currentCorrectAnswers) {
                    answerCounter++;
                    if(answerCounter != 1) {
                        oneStringAnswers.append("_");
                    }
                    oneStringAnswers.append(correctAnswer);
                }

                if(Objects.equals(new String(oneStringAnswers.toString()), new String(playerAnswers)))
                {
                    answerIsCorrect = true;
                }

                break;
            case Constants.QUESTION_ANSWER_TYPE_SINGLE_ANSWER_TEXT:
                oneStringPlayerAnswers.append(playerAnswers);
                for(Object correctAnswer : currentCorrectAnswers) {
                    if(Objects.equals(new String(correctAnswer.toString().toLowerCase()), new String(playerAnswers.toLowerCase())))
                    {
                        answerIsCorrect = true;
                    }
                }
                break;
        }

        //Count correct answers for each player
        if(answerIsCorrect) {
            if(playerNumber == "1") {
                player1CorrectAnswers++;
            }
            else
            {
                player2CorrectAnswers++;
            }
        }

        //Indicator ID
        int indicatorId = getResources().getIdentifier(answerIsCorrect ? "indicator_correct_answer" : "indicator_wrong_answer", "drawable", getPackageName());

        //Show correct indicator image
        if(playerNumber == "1")
        {
            switch (questionCounter) {
                case 1:
                    indicatorQuestion1Player1.setImageResource(indicatorId);
                    break;
                case 2:
                    indicatorQuestion2Player1.setImageResource(indicatorId);
                    break;
                case 3:
                    indicatorQuestion3Player1.setImageResource(indicatorId);
                    break;
                case 4:
                    indicatorQuestion4Player1.setImageResource(indicatorId);
                    break;
                case 5:
                    indicatorQuestion5Player1.setImageResource(indicatorId);
                    break;
            }
        }
        else
        {
            switch (questionCounter) {
                case 1:
                    indicatorQuestion1Player2.setImageResource(indicatorId);
                    break;
                case 2:
                    indicatorQuestion2Player2.setImageResource(indicatorId);
                    break;
                case 3:
                    indicatorQuestion3Player2.setImageResource(indicatorId);
                    break;
                case 4:
                    indicatorQuestion4Player2.setImageResource(indicatorId);
                    break;
                case 5:
                    indicatorQuestion5Player2.setImageResource(indicatorId);
                    break;
            }
        }

        //Return prepared string
        return oneStringPlayerAnswers.toString();
    }

    /**
     * Save instance state
     *
     * @param savedInstanceState - Saved instance state
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //Call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Restore instance state
     *
     * @param savedInstanceState - Saved instance state
     */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        //Call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * onClick method
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        //Perform on click action
        switch(v.getId()) {
            case R.id.button_continue:
                //Confirm answer & generate the question for the next player
                confirmAndGenerateQuestion();
                break;
        }

        if (v.getId() == R.id.question_expand_button_1) {
            if(answersSummary1.isExpanded()) { answersSummary1.collapse(); } else { collapseAllSummaryTabs(); answersSummary1.expand(); }
        }
        else if (v.getId() == R.id.question_expand_button_2) {
            if(answersSummary2.isExpanded()) { answersSummary2.collapse(); } else { collapseAllSummaryTabs(); answersSummary2.expand(); }
        }
        else if (v.getId() == R.id.question_expand_button_3) {
            if(answersSummary3.isExpanded()) { answersSummary3.collapse(); } else { collapseAllSummaryTabs(); answersSummary3.expand(); }
        }
        else if (v.getId() == R.id.question_expand_button_4) {
            if(answersSummary4.isExpanded()) { answersSummary4.collapse(); } else { collapseAllSummaryTabs(); answersSummary4.expand(); }
        }
        else if (v.getId() == R.id.question_expand_button_5) {
            if(answersSummary5.isExpanded()) { answersSummary5.collapse(); } else { collapseAllSummaryTabs(); answersSummary5.expand(); }
        }
    }

    /**
     * Collapse all 'Answers Summary' tabs
     */
    private void collapseAllSummaryTabs()
    {
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
        if(questionAnswered())
        {
            //Show summary if all question answered already
            if(answersTotal == Constants.MAX_ANSWERS)
            {
                //Hide 'Continue' button
                buttonContinue.setVisibility(View.INVISIBLE);

                //Inflate 'Answers Summary'
                inflateSummary();
            }
            else
            {
                //Generate new random question
                getRandomQuestion(false);

                //Show 'Next Player' view
                showNextPlayerView(false);
            }
        }
        else
        {
            //Show Toast when question is not answered yet
            showToast(getString(R.string.not_answered_question),false, FancyToast.WARNING);
        }
    }

    /**
     * Set questions and the corresponding answers (each answer has its index - indexes are starting from 1)
     */
    private void getRandomQuestion(boolean onCreate) {
        //Make sure that question is not generated multiple times on creation (in case if the first continue button was clicked very quickly)
        if(!onCreate || !onCreateQuestionGenerated) {
            //Make sure that there are still questions available & change question only when current player is 'Player 1'
            if (questionsToUse.size() > 0 && currentPlayer == 1) {
                //Increment current question counter
                currentQuestion++;

                //Generate random question index
                Random generator = new Random();
                int randomIndex = generator.nextInt(questionsToUse.size());

                //Get question number
                String randomQuestionNumber = questionsToUse.get(randomIndex);

                //Add question into already used questions array list
                alreadyUsedQuestions.add(randomQuestionNumber);

                //Make sure that the same question can be used only once
                questionsToUse.remove(randomIndex);

                //Set question text
                QuestionView.setText(String.valueOf(questions.get(randomQuestionNumber).get(Constants.QUESTION_KEY_QUESTION)));

                //Show question image
                int questionImageId = getResources().getIdentifier("question_" + randomQuestionNumber, "drawable", getPackageName());
                ImageView questionImageView = findViewById(R.id.question_image);
                questionImageView.setImageResource(questionImageId);

                //Handle answer elements visibility
                //If single choice radio question
                AnswerRadiosView.setVisibility(View.GONE);
                AnswerCheckboxesView.setVisibility(View.GONE);
                AnswerEditTextView.setVisibility(View.GONE);
                if (Arrays.asList(singleChoiceRadioQuestions).contains(randomQuestionNumber)) {
                    quizLastQuestionType = Constants.QUESTION_ANSWER_TYPE_SINGLE_CHOICE_RADIO;

                    AnswerRadiosView.setVisibility(View.VISIBLE);

                    Answer1RadioView.setText(String.valueOf(questions.get(randomQuestionNumber).get(Constants.QUESTION_KEY_ANSWER_1)));
                    Answer2RadioView.setText(String.valueOf(questions.get(randomQuestionNumber).get(Constants.QUESTION_KEY_ANSWER_2)));
                    Answer3RadioView.setText(String.valueOf(questions.get(randomQuestionNumber).get(Constants.QUESTION_KEY_ANSWER_3)));
                    Answer4RadioView.setText(String.valueOf(questions.get(randomQuestionNumber).get(Constants.QUESTION_KEY_ANSWER_4)));
                }
                //If multi choice checkbox question
                else if (Arrays.asList(multipleChoiceCheckboxQuestions).contains(randomQuestionNumber)) {
                    quizLastQuestionType = Constants.QUESTION_ANSWER_TYPE_MULIPLE_CHOICE_CHECKBOXES;

                    AnswerCheckboxesView.setVisibility(View.VISIBLE);

                    Answer1CheckBoxView.setText(String.valueOf(questions.get(randomQuestionNumber).get(Constants.QUESTION_KEY_ANSWER_1)));
                    Answer2CheckBoxView.setText(String.valueOf(questions.get(randomQuestionNumber).get(Constants.QUESTION_KEY_ANSWER_2)));
                    Answer3CheckBoxView.setText(String.valueOf(questions.get(randomQuestionNumber).get(Constants.QUESTION_KEY_ANSWER_3)));
                    Answer4CheckBoxView.setText(String.valueOf(questions.get(randomQuestionNumber).get(Constants.QUESTION_KEY_ANSWER_4)));
                }
                //If single answer text question
                else if (Arrays.asList(singleAnswerTextQuestions).contains(randomQuestionNumber)) {
                    quizLastQuestionType = Constants.QUESTION_ANSWER_TYPE_SINGLE_ANSWER_TEXT;

                    AnswerEditTextView.setVisibility(View.VISIBLE);
                }

                //Change 'stepper' step
                stepperIndicator.setCurrentStep(currentQuestion-1);
            }

            //Clear answer elements values
            AnswerRadiosView.clearCheck();
            Answer1CheckBoxView.setChecked(false);
            Answer2CheckBoxView.setChecked(false);
            Answer3CheckBoxView.setChecked(false);
            Answer4CheckBoxView.setChecked(false);
            AnswerEditTextView.setText("");

            //Update main heading
            MainHeadingView.setText(getString(R.string.current_player, currentPlayer, currentQuestion));

            //Change player
            currentPlayer = currentPlayer == 1 ? 2 : 1;

            //Increment answers total
            answersTotal++;
        }

        //Update onCreateQuestionGenerated flag if question is generated on creation
        if(onCreate) {
            onCreateQuestionGenerated = true;
        }
    }

    /**
     * Show toast
     *
     * @param text
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
        if(previousPlayerSelectedAnswers != null && !previousPlayerSelectedAnswers.isEmpty())
        {
            playerSelectedAnswers = previousPlayerSelectedAnswers;
        }

        //Check if question is answered & remember answers
        switch (quizLastQuestionType) {
            case Constants.QUESTION_ANSWER_TYPE_SINGLE_CHOICE_RADIO:
                int answerRadioButtonId = AnswerRadiosView.getCheckedRadioButtonId();
                if(answerRadioButtonId == -1)
                {
                    return false;
                }
                else
                {
                    RadioButton answerRadioButton = findViewById(answerRadioButtonId);
                    int answerId = Integer.parseInt(answerRadioButton.getTag().toString());

                    playerSelectedAnswers.put(String.valueOf(currentQuestion), String.valueOf(answerId));
                }
                break;
            case Constants.QUESTION_ANSWER_TYPE_MULIPLE_CHOICE_CHECKBOXES:
                if(!Answer1CheckBoxView.isChecked() && !Answer2CheckBoxView.isChecked() && !Answer3CheckBoxView.isChecked() && !Answer4CheckBoxView.isChecked())
                {
                    return false;
                }
                else
                {
                    ArrayList<String> selectedCheckboxAnswers = new ArrayList<String>();
                    if(Answer1CheckBoxView.isChecked()) {
                        selectedCheckboxAnswers.add("1");
                    }
                    if(Answer2CheckBoxView.isChecked()) {
                        selectedCheckboxAnswers.add("2");
                    }
                    if(Answer3CheckBoxView.isChecked()) {
                        selectedCheckboxAnswers.add("3");
                    }
                    if(Answer4CheckBoxView.isChecked()) {
                        selectedCheckboxAnswers.add("4");
                    }
                    //Convert the ArrayList to String
                    StringBuilder oneStringAnswers = new StringBuilder();
                    int answerCounter = 0;
                    for( String answerId : selectedCheckboxAnswers) {
                        answerCounter++;
                        if(answerCounter != 1) {
                            oneStringAnswers.append("_");
                        }
                        oneStringAnswers.append(answerId);
                    }

                    playerSelectedAnswers.put(String.valueOf(currentQuestion), oneStringAnswers.toString());
                }
                break;
            case Constants.QUESTION_ANSWER_TYPE_SINGLE_ANSWER_TEXT:
                String answerText = AnswerEditTextView.getText().toString().trim();
                if(answerText.isEmpty())
                {
                    return false;
                }
                else
                {
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
        String infoMessage = "";
        if(onCreate) {
            infoMessage = getString(R.string.next_player_info, 1);
        }
        else {
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
                Answer1RadioView.setEnabled(false);
                Answer2RadioView.setEnabled(false);
                Answer3RadioView.setEnabled(false);
                Answer4RadioView.setEnabled(false);
                break;
            case Constants.QUESTION_ANSWER_TYPE_MULIPLE_CHOICE_CHECKBOXES:
                Answer1CheckBoxView.setEnabled(false);
                Answer2CheckBoxView.setEnabled(false);
                Answer3CheckBoxView.setEnabled(false);
                Answer4CheckBoxView.setEnabled(false);
                break;
            case Constants.QUESTION_ANSWER_TYPE_SINGLE_ANSWER_TEXT:
                AnswerEditTextView.setEnabled(false);
                AnswerEditTextView.setVisibility(View.GONE);
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
                Answer1RadioView.setEnabled(true);
                Answer2RadioView.setEnabled(true);
                Answer3RadioView.setEnabled(true);
                Answer4RadioView.setEnabled(true);
                break;
            case Constants.QUESTION_ANSWER_TYPE_MULIPLE_CHOICE_CHECKBOXES:
                Answer1CheckBoxView.setEnabled(true);
                Answer2CheckBoxView.setEnabled(true);
                Answer3CheckBoxView.setEnabled(true);
                Answer4CheckBoxView.setEnabled(true);
                break;
            case Constants.QUESTION_ANSWER_TYPE_SINGLE_ANSWER_TEXT:
                AnswerEditTextView.setEnabled(true);
                AnswerEditTextView.setVisibility(View.VISIBLE);
                AnswerEditTextView.setText("");
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
        alreadyUsedQuestions = new ArrayList<String>();
        questionsToUse = new ArrayList<String>();
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
    private void setQuestionsData()
    {
        //Declare hash map for values
        HashMap<String, String> value = new HashMap<>();

        //Single choice radio questions
        for(String questionIndex : singleChoiceRadioQuestions){
            if(questionIndex != "1")
            {
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
        for(String questionIndex : multipleChoiceCheckboxQuestions){
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
            value.put(Constants.QUESTION_KEY_ANSWER_TYPE, Constants.QUESTION_ANSWER_TYPE_MULIPLE_CHOICE_CHECKBOXES);
            questions.put(questionIndex, value);
        }

        //Single answer text questions
        for(String questionIndex : singleAnswerTextQuestions){
            value = new HashMap<>();
            String question = "question_" + questionIndex;
            int questionId = getResources().getIdentifier(question, "string", getPackageName());
            value.put(Constants.QUESTION_KEY_QUESTION, getString(questionId));
            value.put(Constants.QUESTION_KEY_ANSWER_TYPE, Constants.QUESTION_ANSWER_TYPE_SINGLE_ANSWER_TEXT);
            questions.put(questionIndex, value);
        }

        //Answer indexes for Question 1
        ArrayList correct_answer = new ArrayList();
        correct_answer.add(3);
        answers.put("1", correct_answer);

        //Answer indexes for Question 2
        correct_answer = new ArrayList();
        correct_answer.add(3);
        answers.put("2", correct_answer);

        //Answer indexes for Question 3
        correct_answer = new ArrayList();
        correct_answer.add(1);
        answers.put("3", correct_answer);

        //Answer indexes for Question 4
        correct_answer = new ArrayList();
        correct_answer.add(1);
        answers.put("4", correct_answer);

        //Answer indexes for Question 5
        correct_answer = new ArrayList();
        correct_answer.add(4);
        answers.put("5", correct_answer);

        //Answer indexes for Question 6
        correct_answer = new ArrayList();
        correct_answer.add(2);
        answers.put("6", correct_answer);

        //Answer indexes for Question 7
        correct_answer = new ArrayList();
        correct_answer.add(2);
        answers.put("7", correct_answer);

        //Answer indexes for Question 8
        correct_answer = new ArrayList();
        correct_answer.add(2);
        answers.put("8", correct_answer);

        //Answer indexes for Question 9
        correct_answer = new ArrayList();
        correct_answer.add(3);
        answers.put("9", correct_answer);

        //Answer indexes for Question 10
        correct_answer = new ArrayList();
        correct_answer.add(3);
        answers.put("10", correct_answer);

        //Answer indexes for Question 11
        correct_answer = new ArrayList();
        correct_answer.add(1);
        correct_answer.add(2);
        correct_answer.add(4);
        answers.put("11", correct_answer);

        //Answer indexes for Question 12
        correct_answer = new ArrayList();
        correct_answer.add(getString(R.string.question_12_answer_1));
        correct_answer.add(getString(R.string.question_12_answer_1_alt));
        answers.put("12", correct_answer);

        //Answer indexes for Question 13
        correct_answer = new ArrayList();
        correct_answer.add(3);
        answers.put("13", correct_answer);

        //Answer indexes for Question 14
        correct_answer = new ArrayList();
        correct_answer.add(getString(R.string.question_14_answer_1));
        answers.put("14", correct_answer);

        //Answer indexes for Question 15
        correct_answer = new ArrayList();
        correct_answer.add(1);
        correct_answer.add(4);
        answers.put("15", correct_answer);

        //Answer indexes for Question 16
        correct_answer = new ArrayList();
        correct_answer.add(1);
        answers.put("16", correct_answer);

        //Answer indexes for Question 17
        correct_answer = new ArrayList();
        correct_answer.add(3);
        answers.put("17", correct_answer);

        //Answer indexes for Question 18
        correct_answer = new ArrayList();
        correct_answer.add(1);
        correct_answer.add(2);
        correct_answer.add(3);
        answers.put("18", correct_answer);

        //Answer indexes for Question 19
        correct_answer = new ArrayList();
        correct_answer.add(getString(R.string.question_19_answer_1));
        correct_answer.add(getString(R.string.question_19_answer_1_alt));
        answers.put("19", correct_answer);

        //Answer indexes for Question 20
        correct_answer = new ArrayList();
        correct_answer.add(3);
        answers.put("20", correct_answer);

        //Answer indexes for Question 21
        correct_answer = new ArrayList();
        correct_answer.add(getString(R.string.question_21_answer_1));
        answers.put("21", correct_answer);

        //Answer indexes for Question 22
        correct_answer = new ArrayList();
        correct_answer.add(1);
        correct_answer.add(2);
        correct_answer.add(3);
        answers.put("22", correct_answer);

        //Answer indexes for Question 23
        correct_answer = new ArrayList();
        correct_answer.add(1);
        answers.put("23", correct_answer);

        //Answer indexes for Question 24
        correct_answer = new ArrayList();
        correct_answer.add(1);
        correct_answer.add(2);
        correct_answer.add(4);
        answers.put("24", correct_answer);

        //Answer indexes for Question 25
        correct_answer = new ArrayList();
        correct_answer.add(1);
        answers.put("25", correct_answer);

        //Answer indexes for Question 26
        correct_answer = new ArrayList();
        correct_answer.add(2);
        answers.put("26", correct_answer);

        //Answer indexes for Question 27
        correct_answer = new ArrayList();
        correct_answer.add(getString(R.string.question_27_answer_1));
        answers.put("27", correct_answer);

        //Answer indexes for Question 28
        correct_answer = new ArrayList();
        correct_answer.add(3);
        answers.put("28", correct_answer);

        //Answer indexes for Question 29
        correct_answer = new ArrayList();
        correct_answer.add(1);
        correct_answer.add(3);
        correct_answer.add(4);
        answers.put("29", correct_answer);

        //Answer indexes for Question 30
        correct_answer = new ArrayList();
        correct_answer.add(2);
        answers.put("30", correct_answer);

        //Answer indexes for Question 31
        correct_answer = new ArrayList();
        correct_answer.add(2);
        answers.put("31", correct_answer);

        //Answer indexes for Question 32
        correct_answer = new ArrayList();
        correct_answer.add(getString(R.string.question_32_answer_1));
        answers.put("32", correct_answer);

        //Answer indexes for Question 33
        correct_answer = new ArrayList();
        correct_answer.add(3);
        answers.put("33", correct_answer);

        //Answer indexes for Question 34
        correct_answer = new ArrayList();
        correct_answer.add(1);
        correct_answer.add(2);
        correct_answer.add(4);
        answers.put("34", correct_answer);

        //Answer indexes for Question 35
        correct_answer = new ArrayList();
        correct_answer.add(1);
        answers.put("35", correct_answer);

        //Answer indexes for Question 36
        correct_answer = new ArrayList();
        correct_answer.add(2);
        answers.put("36", correct_answer);

        //Answer indexes for Question 37
        correct_answer = new ArrayList();
        correct_answer.add(1);
        answers.put("37", correct_answer);

        //Answer indexes for Question 38
        correct_answer = new ArrayList();
        correct_answer.add(getString(R.string.question_38_answer_1));
        answers.put("38", correct_answer);

        //Answer indexes for Question 39
        correct_answer = new ArrayList();
        correct_answer.add(1);
        answers.put("39", correct_answer);

        //Answer indexes for Question 40
        correct_answer = new ArrayList();
        correct_answer.add(getString(R.string.question_40_answer_1));
        answers.put("40", correct_answer);

        //Answer indexes for Question 41
        correct_answer = new ArrayList();
        correct_answer.add(2);
        answers.put("41", correct_answer);

        //Answer indexes for Question 42
        correct_answer = new ArrayList();
        correct_answer.add(getString(R.string.question_42_answer_1));
        answers.put("42", correct_answer);

        //Answer indexes for Question 43
        correct_answer = new ArrayList();
        correct_answer.add(2);
        answers.put("43", correct_answer);

        //Answer indexes for Question 44
        correct_answer = new ArrayList();
        correct_answer.add(1);
        correct_answer.add(2);
        correct_answer.add(4);
        answers.put("44", correct_answer);

        //Answer indexes for Question 45
        correct_answer = new ArrayList();
        correct_answer.add(getString(R.string.question_45_answer_1));
        answers.put("45", correct_answer);

        //Answer indexes for Question 46
        correct_answer = new ArrayList();
        correct_answer.add(2);
        answers.put("46", correct_answer);

        //Answer indexes for Question 47
        correct_answer = new ArrayList();
        correct_answer.add(4);
        answers.put("47", correct_answer);

        //Answer indexes for Question 48
        correct_answer = new ArrayList();
        correct_answer.add(2);
        answers.put("48", correct_answer);

        //Answer indexes for Question 49
        correct_answer = new ArrayList();
        correct_answer.add(3);
        answers.put("49", correct_answer);

        //Answer indexes for Question 50
        correct_answer = new ArrayList();
        correct_answer.add(1);
        answers.put("50", correct_answer);

        //Answer indexes for Question 51
        correct_answer = new ArrayList();
        correct_answer.add(getString(R.string.question_51_answer_1));
        correct_answer.add(getString(R.string.question_51_answer_1_alt));
        answers.put("51", correct_answer);

        //Answer indexes for Question 52
        correct_answer = new ArrayList();
        correct_answer.add(2);
        answers.put("52", correct_answer);

        //Answer indexes for Question 53
        correct_answer = new ArrayList();
        correct_answer.add(getString(R.string.question_53_answer_1));
        correct_answer.add(getString(R.string.question_53_answer_1_alt));
        correct_answer.add(getString(R.string.question_53_answer_1_alt_2));
        answers.put("53", correct_answer);

        //Answer indexes for Question 54
        correct_answer = new ArrayList();
        correct_answer.add(1);
        correct_answer.add(2);
        correct_answer.add(3);
        answers.put("54", correct_answer);

        //Answer indexes for Question 55
        correct_answer = new ArrayList();
        correct_answer.add(1);
        correct_answer.add(3);
        correct_answer.add(4);
        answers.put("55", correct_answer);
    }

    /**
     * Hide action bar
     */
    private void hideActionBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}