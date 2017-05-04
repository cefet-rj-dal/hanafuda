package br.gpca.hanafuda.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import br.gpca.hanafuda.kernel.Card;

public class CustomAdapterDeck extends ArrayAdapter<Card> {

    /*
     * Used to instantiate layout XML file into its corresponding View objects
     */
    private final LayoutInflater inflater;

    /*
     * each list item layout ID
     */
    private final int resourceId;


    public CustomAdapterDeck(Context context, int resource, ArrayList<Card> object) {
        super(context, resource, object);
        this.inflater = LayoutInflater.from(context);
        this.resourceId = resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //get the card from position
        Card card = getItem(position);
        // get a new View no matter recycling or ViewHolder FIXME
        convertView = inflater.inflate(resourceId, parent, false);

        //get all object from view
        ImageView image = (ImageView) convertView.findViewById(R.id.image);

        image.setImageResource(R.drawable.card);

        return convertView;

    }
}
