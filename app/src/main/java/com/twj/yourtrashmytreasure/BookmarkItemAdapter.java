package com.twj.yourtrashmytreasure;

import android.content.Context;
import android.content.Intent;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

public class BookmarkItemAdapter extends FirestoreRecyclerAdapter<ItemHelper, BookmarkItemAdapter.ItemHolder>{

    private FirebaseFirestore firebase_database=FirebaseFirestore.getInstance();
    private CollectionReference bookmarkRef = firebase_database.collection("users");
    private OnItemClickListener listener;
    private Context mContext;
    private String bookmark_id;

    public BookmarkItemAdapter(@NonNull FirestoreRecyclerOptions<ItemHelper> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ItemHolder holder, int position, @NonNull ItemHelper model) {
        if(!StartActivity.list_bookmark.contains(model.getItem_redeem_id())){
        }
        holder.label_item_name.setText(model.getItem_name());
        holder.label_item_category.setText(model.getItem_category());
        holder.label_item_used_status.setText(model.getUsed_status());
        Picasso.with(mContext).load(model.getItem_image_url()).fit().centerCrop().into(holder.item_image);
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_item_view,parent,false);
        mContext = parent.getContext();
        return new ItemHolder(v);
    }

    public void deleteItem(int position){
        ItemHelper item = getSnapshots().getSnapshot(position).toObject(ItemHelper.class);
        bookmarkRef.whereEqualTo("item_redeem_id",item.getItem_redeem_id()).whereEqualTo("user_id",StartActivity.USER.getId())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    bookmark_id = documentSnapshot.getId();
                }
                bookmarkRef.document(bookmark_id).delete();

            }
        });
        StartActivity.list_bookmark.remove(item.getItem_redeem_id());
        Toast.makeText(mContext, "Bookmark deleted - Swipe Down to Refresh", Toast.LENGTH_LONG).show();
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
        //Button btn_unbookmark_item;
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            label_item_name = itemView.findViewById(R.id.text_item_name);
            label_item_category = itemView.findViewById(R.id.text_item_category);
            label_item_used_status = itemView.findViewById(R.id.text_item_used_status);
            item_image = itemView.findViewById(R.id.item_image);
            //btn_unbookmark_item = itemView.findViewById(R.id.btn_unbookmark_item);

            /*btn_unbookmark_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAbsoluteAdapterPosition();
                    if(position != -1) {
                        ItemHelper item = getSnapshots().getSnapshot(position).toObject(ItemHelper.class);
                        bookmarkRef.whereEqualTo("item_redeem_id",item.getItem_redeem_id()).whereEqualTo("user_id",StartActivity.USER.getId())
                                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                    bookmark_id = documentSnapshot.getId();
                                }
                                bookmarkRef.document(bookmark_id).delete();

                            }
                        });
                        StartActivity.list_bookmark.remove(item.getItem_redeem_id());
                        btn_unbookmark_item.setText("Deleted");
                        btn_unbookmark_item.setEnabled(false);
                        itemView.setVisibility(View.GONE);
                        Toast.makeText(mContext, "Bookmark deleted - Refresh to see the updates", Toast.LENGTH_LONG).show();
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position,getItemCount()-position);
                    }
                }
            });*/

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
