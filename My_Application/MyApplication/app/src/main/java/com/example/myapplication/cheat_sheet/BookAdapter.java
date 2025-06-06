package com.example.myapplication.cheat_sheet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookVH> {

    private List<Book> bookList;

    public BookAdapter(List<Book> bookList) {
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_card, parent, false);
        return new BookVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookVH holder, int position) {
        Book book = bookList.get(position);
        holder.heading.setText(book.getName());
        holder.description.setText(
                (!(book.getAuthor().equals("Neznámý autor"))?"▪ autor: " +book.getAuthor() + "\n\n":"▪ "+book.getAuthor()+"\n\n") +
                (!(book.getGenre()==null)?"▪ žánr: " + book.getGenre() + "\n\n":"") +
                (!(book.getMovement()==null)?"▪ směr: "  + book.getMovement() + "\n\n":"") +
                (!(book.getDruh()==null)?"▪ druh: " + book.getDruh() + "\n\n":"") +
                (!(book.getPublishYear()==null)?"▪ rok vydání: " + book.getPublishYear() + "\n":"")
        );



        boolean isExpandable = bookList.get(position).isExpandable();
        holder.expandable.setVisibility(isExpandable ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public class BookVH extends RecyclerView.ViewHolder{

        TextView heading, description;
        LinearLayout linearLayout;
        RelativeLayout expandable;


        public BookVH(@NonNull View itemView) {
            super(itemView);

            heading = itemView.findViewById(R.id.headingSheet);
            description = itemView.findViewById(R.id.descriptionSheet);

            linearLayout = itemView.findViewById(R.id.linearLayout);
            expandable = itemView.findViewById(R.id.expandable);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Book book = bookList.get(getAdapterPosition());
                    book.setExpandable(!book.isExpandable());
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }
}
