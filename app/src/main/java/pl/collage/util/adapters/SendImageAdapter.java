package pl.collage.util.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import pl.collage.R;
import pl.collage.sendimage.SendImageListener;
import pl.collage.util.models.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SendImageAdapter extends RecyclerView.Adapter<SendImageAdapter.ViewHolder> {

    private List<User> friendsList;
    private SendImageListener sendImageListener;

    public SendImageAdapter(List<User> friendsList, SendImageListener sendImageListener) {
        this.sendImageListener = sendImageListener;
        this.friendsList = friendsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_send_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final User friend = friendsList.get(position);
        if (friend.sendingStarted) {
            holder.button.setVisibility(View.INVISIBLE);
            holder.progressBar.setVisibility(View.VISIBLE);
        } else if (friend.sendingFinished) {
            holder.button.setVisibility(View.INVISIBLE);
            holder.progressBar.setVisibility(View.GONE);
        }

        holder.textView.setText(friendsList.get(position).fullName);
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friend.sendingStarted = true;
                sendImageListener.onImageUploadStarted(friend);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_send_image_text_view)
        TextView textView;

        @BindView(R.id.item_send_image_button)
        public Button button;

        @BindView(R.id.item_send_progress_bar)
        public ProgressBar progressBar;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
