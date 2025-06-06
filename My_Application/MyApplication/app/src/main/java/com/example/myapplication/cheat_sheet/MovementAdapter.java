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

public class MovementAdapter extends RecyclerView.Adapter<MovementAdapter.MovementVH> {

    private List<Movement> movementList;

    public MovementAdapter(List<Movement> movementList) {
        this.movementList = movementList;
    }

    @NonNull
    @Override
    public MovementVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_card, parent, false);
        return new MovementVH(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull MovementVH holder, int position) {
        Movement movement = movementList.get(position);
        holder.heading.setText(movement.getName());
        holder.description.setText(
                ((!movement.getAuthors().isEmpty())?"▪ autoři: " +String.join(", ", movement.getAuthors()) + "\n\n":"") +
                (!(movement.getCentury()==null)?"▪ období: " +movement.getCentury() + "\n\n":"") +
                (!(movement.getSign()==null)?"▪ znaky: " +movement.getSign() + "\n":"")
        );

        boolean isExpandable = movementList.get(position).isExpandable();
        holder.expandable.setVisibility(isExpandable ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {
        return movementList.size();
    }

    public class MovementVH extends RecyclerView.ViewHolder {
        TextView heading, description;
        LinearLayout linearLayout;
        RelativeLayout expandable;
        public MovementVH(@NonNull View itemView) {
            super(itemView);

            heading = itemView.findViewById(R.id.headingSheet);
            description = itemView.findViewById(R.id.descriptionSheet);

            linearLayout = itemView.findViewById(R.id.linearLayout);
            expandable = itemView.findViewById(R.id.expandable);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Movement movement = movementList.get(getAdapterPosition());
                    movement.setExpandable(!movement.isExpandable());
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }
}
