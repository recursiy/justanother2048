package ru.recursiy.justanother2048;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.TextView;

/**
 * Adapter to show game field
 */
public class GameAdapter extends BaseAdapter {
    //getView, getItemId, getItem, getCount

    private int fieldValues[][];
    private int fieldSize;
    private LayoutInflater inflater;

    GameAdapter(Context context, int[][] game2048)
    {
        fieldValues = game2048;
        fieldSize = fieldValues.length;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        return fieldSize*fieldSize;
    }

    @Override
    public Object getItem(int position)
    {
        return fieldValues[position/fieldSize][position%fieldSize];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.field_element, parent, false);
        }

        Integer value = (Integer) getItem(position);
        TextView tv = ((TextView) view.findViewById(R.id.field_text));
        if (value > 0)
            tv.setText(value.toString());
        else
            tv.setText("");



        int color;
        switch(value)
        {
            case 0:
                color = R.color.color0;
                break;
            case 2:
            case 4:
                color = R.color.color2;
                break;
            case 8:
                color = R.color.color8;
                break;
            case 16:
                color = R.color.color16;
                break;
            case 32:
                color = R.color.color32;
                break;
            case 64:
                color = R.color.color64;
                break;
            case 128:
                color = R.color.color128;
                break;
            case 256:
                color = R.color.color256;
                break;
            case 512:
                color = R.color.color128;
                break;
            case 1024:
                color = R.color.color1024;
                break;
            case 2048:
                color = R.color.color2048;
                break;
            default: //4096 and more
                color = R.color.color4096;
        }
        view.findViewById(R.id.field_background).setBackgroundResource(color);
        return view;
    }
}
