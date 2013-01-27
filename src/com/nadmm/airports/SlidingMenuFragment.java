/*
 * FlightIntel for Pilots
 *
 * Copyright 2011-2013 Nadeem Hasan <nhasan@nadmm.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */

package com.nadmm.airports;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nadmm.airports.aeronav.ChartsDownloadActivity;
import com.nadmm.airports.library.LibraryActivity;
import com.nadmm.airports.scratchpad.ScratchPadActivity;
import com.nadmm.airports.tfr.TfrActivity;

public class SlidingMenuFragment extends ListFragmentBase {

    public static final int ITEM_ID_AFD = 0;
    public static final int ITEM_ID_TFR = 1;
    public static final int ITEM_ID_LIBRARY = 2;
    public static final int ITEM_ID_SCRATCHPAD = 3;
    public static final int ITEM_ID_CHARTS = 4;

    private static final String SELECTED_ITEM = "SELECTED_ITEM";

    private final Handler mHandler = new Handler();

    @Override
    public void onActivityCreated( Bundle savedInstanceState ) {
        super.onActivityCreated( savedInstanceState );
        ListView lv = getListView();
        lv.setBackgroundResource( R.color.slidingmenu_background );
        lv.setCacheColorHint( 0x00000000 );
        lv.setChoiceMode( ListView.CHOICE_MODE_SINGLE );

        if ( savedInstanceState != null ) {
            int position = savedInstanceState.getInt( SELECTED_ITEM, 0 );
            lv.setItemChecked( position, true );
        } else {
            lv.setItemChecked( 0, true );
        }

        setCursor( new SlidingMenuCursor() );
    }

    @Override
    protected void setFragmentContentShown( boolean shown ) {
    }

    @Override
    protected View createContentView( View view ) {
        return view;
    }

    @Override
    public void onSaveInstanceState( Bundle outState ) {
        super.onSaveInstanceState( outState );
        outState.putInt( SELECTED_ITEM, getListView().getCheckedItemPosition() );
    }

    @Override
    protected CursorAdapter newListAdapter( Context context, Cursor c ) {
        return new SlidingMenuAdapter( getActivity(), c );
    }

    @Override
    protected void onListItemClick( ListView l, View v, int position ) {
        l.setItemChecked( position, true );
        long id = l.getItemIdAtPosition( position );
        if ( id == ITEM_ID_AFD ) {
            startActivity( getActivityBase().getHomeActivityClass() );
        } else if ( id == ITEM_ID_TFR ) {
            startActivity( TfrActivity.class );
        } else if ( id == ITEM_ID_LIBRARY ) {
            startActivity( LibraryActivity.class );
        } else if ( id == ITEM_ID_SCRATCHPAD ) {
            startActivity( ScratchPadActivity.class );
        } else if ( id == ITEM_ID_CHARTS ) {
            startActivity( ChartsDownloadActivity.class );
        }
    }

    public void setActivatedItem( int position ) {
        getListView().setItemChecked( position, true );
    }

    protected void startActivity( final Class<?> clss ) {
        ActivityBase activity = getActivityBase();
        if ( clss != activity.getClass() ) {
            activity.setContentShown( false );
            if ( getActivity().getClass() != clss ) {
                mHandler.postDelayed( new Runnable() {

                    @Override
                    public void run() {
                        Intent activity = new Intent( getActivity(), clss );
                        startActivity( activity );
                    }
                }, 200 );
            }
        }
        activity.toggle();
    }

    protected class SlidingMenuAdapter extends CursorAdapter {

        public SlidingMenuAdapter( Context context, Cursor c ) {
            super( context, c, 0 );
        }

        @Override
        public void bindView( View view, Context context, Cursor c ) {
            String text = c.getString( c.getColumnIndex( SlidingMenuCursor.ITEM_TEXT ) );
            int id = c.getInt( c.getColumnIndex( SlidingMenuCursor.ITEM_ICON ) );
            TextView tv = (TextView) view.findViewById( R.id.item_text );
            tv.setText( text );
            ImageView iv = (ImageView) view.findViewById( R.id.item_icon );
            iv.setImageResource( id );
        }

        @Override
        public View newView( Context context, Cursor c, ViewGroup container ) {
            return inflate( R.layout.sliding_menu_item, container );
        }
    }

    protected static class SlidingMenuCursor extends MatrixCursor {

        private static final String ITEM_TEXT = "ITEM_TEXT";
        private static final String ITEM_ICON = "ITEM_ICON";

        private final static String[] sColumnNames = new String[]
                { BaseColumns._ID, ITEM_TEXT, ITEM_ICON };

        public SlidingMenuCursor() {
            super( sColumnNames );
            newRow().add( ITEM_ID_AFD ).add( "A/FD" ).add( R.drawable.airport );
            newRow().add( ITEM_ID_TFR ).add( "TFRs" ).add( R.drawable.stop );
            newRow().add( ITEM_ID_LIBRARY ).add( "Library" ).add( R.drawable.library );
            newRow().add( ITEM_ID_SCRATCHPAD ).add( "Scratch Pad" ).add( R.drawable.notepad );
            newRow().add( ITEM_ID_CHARTS ).add( "Manage Charts" ).add( R.drawable.folder );
        }

    }

}
