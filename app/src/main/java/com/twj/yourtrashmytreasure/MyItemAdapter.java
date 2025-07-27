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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class MyItemAdapter extends FirestoreRecyclerAdapter<ItemHelper, MyItemAdapter.ItemHolder>{

    private FirebaseFirestore firebase_database=FirebaseFirestore.getInstance();
    private CollectionReference bookmarkRef = firebase_database.collection("users");
    private OnItemClickListener listener;
    private Context mContext;
    private String image_url;

    public MyItemAdapter(@NonNull FirestoreRecyclerOptions<ItemHelper> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ItemHolder holder, int position, @NonNull ItemHelper model) {
        image_url = model.getItem_image_url();
        holder.label_item_name.setText(model.getItem_name());
        holder.label_item_category.setText(model.getItem_category());
        holder.label_item_used_status.setText(model.getUsed_status());
        holder.label_item_redeemed_status.setText(Boolean.toString(model.getRedeemed_status()));
        Picasso.with(mContext).load(model.getItem_image_url()).fit().centerCrop().into(holder.item_image);
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_item_view,parent,false);
        mContext = parent.getContext();
        return new ItemHolder(v);
    }

    public void deleteItem(int position){
        StorageReference photo_ref = FirebaseStorage.getInstance().getReferenceFromUrl(image_url);
        photo_ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                getSnapshots().getSnapshot(position).getReference().delete();
                Toast.makeText(mContext,"Item deleted!",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });

    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    class ItemHolder extends RecyclerView.ViewHolder{
        TextView label_item_name;
        TextView label_item_category;
        TextView label_item_used_status;
        TextView label_item_redeemed_status;
        ImageView item_image;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            label_item_name = itemView.findViewById(R.id.text_item_name);
            label_item_category = itemView.findViewById(R.id.text_item_category);
            label_item_used_status = itemView.findViewById(R.id.text_item_used_status);
            label_item_redeemed_status = itemView.findViewById(R.id.text_item_redeemed_status);
            item_image = itemView.findViewById(R.id.item_image);



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
