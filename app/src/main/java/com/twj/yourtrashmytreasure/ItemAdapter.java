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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ItemAdapter extends FirestoreRecyclerAdapter<ItemHelper,ItemAdapter.ItemHolder>{

    private FirebaseFirestore firebase_database=FirebaseFirestore.getInstance();
    private CollectionReference bookmarkRef = firebase_database.collection("users");
    private OnItemClickListener listener;
    private Context mContext;

    public ItemAdapter(@NonNull FirestoreRecyclerOptions<ItemHelper> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ItemHolder holder, int position, @NonNull ItemHelper model) {
        if(StartActivity.list_bookmark.contains(model.getItem_redeem_id())){
            holder.btn_bookmark_item.setEnabled(false);
            holder.btn_bookmark_item.setImageResource(R.drawable.ic_baseline_bookmark_24);
            //holder.btn_bookmark_item.setText("Bookmarked");
        }
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
        FloatingActionButton btn_bookmark_item;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            label_item_name = itemView.findViewById(R.id.text_item_name);
            label_item_category = itemView.findViewById(R.id.text_item_category);
            label_item_used_status = itemView.findViewById(R.id.text_item_used_status);
            item_image = itemView.findViewById(R.id.item_image);
            btn_bookmark_item = itemView.findViewById(R.id.btn_bookmark_item);

            btn_bookmark_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAbsoluteAdapterPosition();
                    if(position != -1) {
                        ItemHelper item = getSnapshots().getSnapshot(position).toObject(ItemHelper.class);
                        Bookmark bookmark_object = new Bookmark(item.getItem_redeem_id(),StartActivity.USER.getId());
                        bookmarkRef.document().set(bookmark_object);
                        StartActivity.list_bookmark.remove("");
                        if(StartActivity.list_bookmark.size()<9){
                            StartActivity.list_bookmark.add(item.getItem_redeem_id());
                            Toast.makeText(mContext, "Bookmarked", Toast.LENGTH_SHORT).show();
                            //btn_bookmark_item.setText("Bookmarked");
                            btn_bookmark_item.setImageResource(R.drawable.ic_baseline_bookmark_24);
                            btn_bookmark_item.setEnabled(false);
                        }else{
                            Toast.makeText(mContext, "Bookmark Limit Reached : 9", Toast.LENGTH_SHORT).show();
                        }


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
