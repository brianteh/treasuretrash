package com.twj.yourtrashmytreasure;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class AddItemAdapter extends FirestoreRecyclerAdapter<ItemHelper, AddItemAdapter.ItemHolder>{

    private OnItemClickListener listener;
    private Context mContext;

    public AddItemAdapter(@NonNull FirestoreRecyclerOptions<ItemHelper> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ItemHolder holder, int position, @NonNull ItemHelper model) {
        holder.label_item_name.setText(model.getItem_name());
        holder.label_item_category.setText(model.getItem_category());
        holder.label_item_used_status.setText(model.getUsed_status());
        Picasso.with(mContext).load(model.getItem_image_url()).fit().centerCrop().into(holder.item_image);
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view,parent,false);
        mContext = parent.getContext();
        return new ItemHolder(v);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    class ItemHolder extends RecyclerView.ViewHolder{
        TextView label_item_name;
        TextView label_item_category;
        TextView label_item_used_status;
        ImageView item_image;
        Button btn_bookmark_item;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            label_item_name = itemView.findViewById(R.id.label_item_name);
            label_item_category = itemView.findViewById(R.id.label_item_category);
            label_item_used_status = itemView.findViewById(R.id.label_item_used_status);
            item_image = itemView.findViewById(R.id.item_image);
            btn_bookmark_item = itemView.findViewById(R.id.btn_bookmark_item);

            btn_bookmark_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAbsoluteAdapterPosition();
                    if(position != -1) {
                        ItemHelper item = getSnapshots().getSnapshot(position).toObject(ItemHelper.class);
                        Toast.makeText(mContext, "Bookmarked" + item.getItem_image_url(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAbsoluteAdapterPosition();
                    if(position != -1 && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);

                    }
                }
            });
        }
    }
    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
