package com.bignerdranch.android.selfjournal.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bignerdranch.android.selfjournal.MainActivity;
import com.bignerdranch.android.selfjournal.R;
import com.bignerdranch.android.selfjournal.model.Journal;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

public class JournalRecyclerAdapter extends RecyclerView.Adapter<JournalRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Journal> journalList;
    private ImageButton shareButton;

    public JournalRecyclerAdapter(Context context, List<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }

    @NonNull
    @Override
    public JournalRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.journal_row, parent, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalRecyclerAdapter.ViewHolder viewHolder, int position) {
        String imageUrl;
        Journal journal = journalList.get(position);
        viewHolder.title.setText(journal.getTitle());
        viewHolder.thoughts.setText(journal.getThought());
        viewHolder.name.setText(journal.getUserName());
        imageUrl = journal.getImageUrl();
        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(journal.getTimeAdded().getSeconds() * 1000);

        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.batman)
                .fit()
                .into(viewHolder.image);
        viewHolder.dateAdded.setText(timeAgo);
    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "WOW" ;
        public TextView title, thoughts, dateAdded, name;
        public ImageView image;
        String userId;
        String username;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            title = itemView.findViewById(R.id.journal_title_list);
            thoughts = itemView.findViewById(R.id.journal_thought_list);
            dateAdded = itemView.findViewById(R.id.journal_timestamp_list);
            image = itemView.findViewById(R.id.journal_image_list);
            name = itemView.findViewById(R.id.journal_row_username);
            shareButton = itemView.findViewById(R.id.journal_row_share_button);

            Journal journal = new Journal();

            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Picasso.get().load(journal.getImageUrl()).into(
                            new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    Intent intent = new Intent(Intent.ACTION_SEND);
                                    intent.setType("image/*");
                                    intent.putExtra(Intent.EXTRA_SUBJECT, "Here I share my thoughts with you...");
                                    intent.putExtra(Intent.EXTRA_TEXT, MessageFormat.format(
                                            "Title: {0}\n" + "Thought: {1}\n",
                                                    journal.getTitle(), journal.getThought()));
                                    intent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
                                    ctx.startActivity(Intent.createChooser(intent, ctx.getResources()
                                    .getText(R.string.share_text)));
                                }

                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                    Log.d("Failed", "onBitmapFailed  ");
                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {
                                    Log.d("Failed", "onPrepareLoadFailed  ");
                                }
                            }
                    );
                }
            });

        }

        public Uri getLocalBitmapUri(Bitmap bmp) {
            Uri bmpUri = null;
            try {
                File imagePath = new File(context.getExternalFilesDir(null).toString());
                if(!imagePath.exists()) {
                    boolean hasBeenCreated = imagePath.mkdirs();
                    Log.d(TAG, "imagePath does exist. was it created?" + hasBeenCreated);
                }
                File newFile = new File(imagePath, "share_image_" + System.currentTimeMillis() + ".png");
                FileOutputStream out = new FileOutputStream(newFile);
                bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
                String authorities = context.getPackageName() + ".fileprovider";
                bmpUri = FileProvider.getUriForFile(context, authorities, newFile);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bmpUri;
        }
    }
}
