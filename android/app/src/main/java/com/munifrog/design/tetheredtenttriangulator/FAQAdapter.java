package com.munifrog.design.tetheredtenttriangulator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.ViewHolder> {

   private final String [] mQuestions;
   private final String [] mAnswers;

   public FAQAdapter(String[] questions, String[] answers) {
      // Assume each question and answer combination is located at the same index
      mQuestions = questions;
      mAnswers = answers;
      // Any length mismatch will show up during development
      assert(mQuestions.length == mAnswers.length);
   }

   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      // Inflate the item that will be recycled thereafter
      View view = LayoutInflater.from(parent.getContext())
              .inflate(R.layout.item_faq, parent, false);
      return new ViewHolder(view);
   }

   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      // Set the question and answer for this position
      holder.getQuestion().setText(mQuestions[position]);
      holder.getAnswer().setText(mAnswers[position]);
   }

   @Override
   public int getItemCount() {
      return mQuestions.length;
   }

   public static class ViewHolder extends RecyclerView.ViewHolder {
      private final TextView vQuestion;
      private final TextView vAnswer;
      private boolean isShowing;

      public ViewHolder(@NonNull View itemView) {
         super(itemView);

         vQuestion = itemView.findViewById(R.id.question);
         vAnswer = itemView.findViewById(R.id.answer);
         isShowing = false;
         setVisibility();

         vQuestion.setOnClickListener(view -> {
            toggleVisibility();
         });
         vAnswer.setOnClickListener(view -> {
            toggleVisibility();
         });
      }

      public TextView getQuestion() {
         return vQuestion;
      }

      public TextView getAnswer() {
         return vAnswer;
      }

      private void toggleVisibility() {
         isShowing = !isShowing;
         setVisibility();
      }

      private void setVisibility() {
         vAnswer.setVisibility((isShowing ? View.VISIBLE : View.GONE));
      }
   }
}
