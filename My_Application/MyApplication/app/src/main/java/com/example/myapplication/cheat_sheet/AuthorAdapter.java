package com.example.myapplication.cheat_sheet;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class AuthorAdapter extends RecyclerView.Adapter<AuthorAdapter.AuthorVH> {

    private List<Author> authorList;

    public AuthorAdapter(List<Author> authorList) {
        this.authorList = authorList;
    }


    @NonNull
    @Override
    public AuthorVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_card, parent, false);
        return new AuthorVH(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull AuthorVH holder, int position) {
        Author author = authorList.get(position);
        holder.heading.setText(author.getName());
        holder.description.setText(
                (!(author.getCountry()== null) ?"▪ země původu: " +author.getCountry() + "\n \n":"") +
                ((!author.getBooks().isEmpty())?"▪ dílo: " +String.join(", ", author.getBooks()) + "\n\n":"") +
                (!(author.getMovement() == null)?"▪ hnutí: " +author.getMovement() + "\n" : "")
        );

        boolean isExpandable = authorList.get(position).isExpandable();
        holder.expandable.setVisibility(isExpandable ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {
        return authorList.size();
    }

    public class AuthorVH extends RecyclerView.ViewHolder {
        TextView heading, description;
        LinearLayout linearLayout;
        RelativeLayout expandable;
        public AuthorVH(@NonNull View itemView) {
            super(itemView);

            heading = itemView.findViewById(R.id.headingSheet);
            description = itemView.findViewById(R.id.descriptionSheet);

            linearLayout = itemView.findViewById(R.id.linearLayout);
            expandable = itemView.findViewById(R.id.expandable);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Author author = authorList.get(getAdapterPosition());
                    author.setExpandable(!author.isExpandable());
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }
}
