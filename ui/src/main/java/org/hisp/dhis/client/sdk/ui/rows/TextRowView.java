package org.hisp.dhis.client.sdk.ui.rows;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.FormEntity;
import org.hisp.dhis.client.sdk.ui.models.FormEntityText;

public class TextRowView implements RowView {
    private RowViewAdapter.Type type;

    public TextRowView(RowViewAdapter.Type type) {
        this.type = type;
    }

    public TextRowView() {
        this.type = RowViewAdapter.Type.DATA_ENTRY;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
        if(RowViewAdapter.Type.DATA_ENTRY.equals(this.type)) {
            return new TextViewHolder(inflater.inflate(
                    R.layout.recyclerview_row_textview, parent, false));
        }
        else {
            return new TextViewDataViewHolder(inflater.inflate(
                    R.layout.recyclerview_row_textview_dataview, parent, false));
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, FormEntity formEntity) {
        FormEntityText formEntityText = ((FormEntityText) formEntity);
        if(RowViewAdapter.Type.DATA_ENTRY.equals(this.type)) {
            ((TextViewHolder) viewHolder).update(formEntityText);
        }
        else {
            ((TextViewDataViewHolder) viewHolder).update(formEntityText);
        }
    }

    public RowViewAdapter.Type getType() {
        return type;
    }

    private static class TextViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewLabel;
        final TextView textViewValue;

        public TextViewHolder(View itemView) {
            super(itemView);

            textViewLabel = (TextView) itemView.findViewById(R.id.textview_row_label);
            textViewValue = (TextView) itemView.findViewById(R.id.textview_row_textview);
        }

        public void update(FormEntityText entityText) {
            textViewLabel.setText(entityText.getLabel());
            textViewValue.setText(entityText.getValue());
        }
    }

    private static class TextViewDataViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewValue;
        final AppCompatImageView imageView;

        public TextViewDataViewHolder(View itemView) {
            super(itemView);

            textViewValue = (TextView) itemView.findViewById(R.id.textview_row_textview);
            imageView = (AppCompatImageView) itemView.findViewById(R.id.textview_row_textview_dataview_imageview);
        }

        public void update(FormEntityText entityText) {
            textViewValue.setText(entityText.getValue());
        }
    }
}
