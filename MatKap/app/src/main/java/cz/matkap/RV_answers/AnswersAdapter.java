package cz.matkap.RV_answers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cz.matkap.QuestionActivity;

import cz.matkap.R;

import java.util.List;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.AnswerVH> {

    private List<QuestionActivity.AnsweredQuestion> answeredQuestions;

    public AnswersAdapter(List<QuestionActivity.AnsweredQuestion> answeredQuestions) {
        this.answeredQuestions = answeredQuestions;
    }

    @NonNull
    @Override
    public AnswerVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.answers_card, parent, false);
        return new AnswerVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerVH holder, int position) {
        QuestionActivity.AnsweredQuestion aq = answeredQuestions.get(position);
        holder.questionText.setText((position + 1) + ". " + aq.getQuestionText());
        holder.rightAnswer.setText("\t ▪  " + aq.getRightAnswer());
        if(aq.getWrongAnswer() != null) {
            holder.wrongAnswer.setText("\t ▪  " + aq.getWrongAnswer());
        } else holder.wrongAnswer.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return answeredQuestions.size();
    }

    public class AnswerVH extends RecyclerView.ViewHolder {
        TextView questionText, rightAnswer, wrongAnswer;
        public AnswerVH(@NonNull View view) {
            super(view);

            questionText = view.findViewById(R.id.questiontext);
            rightAnswer = view.findViewById(R.id.answerRight);
            wrongAnswer = view.findViewById(R.id.answerWrong);
        }
    }
}
