package codebind.example.calenderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static android.view.FrameMetrics.ANIMATION_DURATION;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TableLayout mainTable; //Main matrix
    LayoutInflater l; //Inflator
    View rowview; //View for each row
    LinearLayout infoLayout; //Layout for info of day

    ArrayList<String> months = new ArrayList<String>(
            Arrays.asList("January","February","March","April","May","June","July","August","September","October","November","December"));
    ArrayList<Integer> days = new ArrayList<Integer>(
            Arrays.asList(31, 28, 31,30,31,30,31,31,30,31,30,31)); //Months

    ArrayList<Integer> startofMonth = new ArrayList<Integer>(Arrays.asList(1,4,4,0,2,5,0,3,6,1,4,8));

    HashMap<String, Integer> holidays = new HashMap<String, Integer>(); // Holidays list

    HashMap<String, String> holidaysEvents = new HashMap<String, String>(); // Holidays Events list

    HashMap<String, String> holidaysEventsInfo = new HashMap<String, String>(); // Holidays Events Info list

    //Temperory Events list ==> This can be retrieved API call for better performance
    ArrayList<String> tmpevents = new ArrayList<>(
            Arrays.asList("Republic Day","Poornima","Holi","Eid","Feast","Ganesh Chaturthi","Christmas","Diwali")
    );
    //Temperory Events Informaation list ==> This can be retrieved API call for better performance
    ArrayList<String> tmpeventsInfo = new ArrayList<>(
            Arrays.asList("Day when India was said to be republic","Festival of Lord","Festival of Colors","Biggest festival among Muslim community","Festival of Christians","Festival of Lord Ganesha","Biggest Festival in December","Festival of lights")
    );

    TextView month; //Month value

    Integer currentMonth = 0; //current month value

    ImageButton goleft,goright;
    TextView infoTitle; //Title of the event
    TextView infoDetails; //Details of the event

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Calender");

        mainTable = (TableLayout) findViewById(R.id.mytable);
        month = (TextView)findViewById(R.id.month_value);
        infoTitle = (TextView)findViewById(R.id.info_title);
        infoDetails = (TextView)findViewById(R.id.info_details);
        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mainTable.getVisibility()==View.GONE){
                    //HIDE CALENDER
                    Transition transition = new Fade();
                    transition.setDuration(600);
                    transition.addTarget(R.id.mytable);
                    TransitionManager.beginDelayedTransition(mainTable, transition);
                    mainTable.setVisibility(View.VISIBLE);
                }else {
                    //UNHIDE CALENDER
                    Transition transition = new Fade();
                    transition.setDuration(600);
                    transition.addTarget(R.id.mytable);
                    TransitionManager.beginDelayedTransition(mainTable, transition);
                    mainTable.setVisibility(View.GONE);

                }
            }
        });

        setHolidays(); // SET HOLIDAY LIST
        setCalender(); // INITIALIZE CALENDER
        setEvents(); //SET EVENTS ARRAY

        goleft = (ImageButton)findViewById(R.id.leftbtn);
        goright = (ImageButton)findViewById(R.id.rightbtn);
        goleft.setOnClickListener(this);
        goright.setOnClickListener(this);

        infoTitle.setOnClickListener(this);

    }

    private void setEvents() {

        // Print keys and values
        int i=0;
        for (String key : holidays.keySet()) {
            holidaysEvents.put(key+holidays.get(key),tmpevents.get(i));
            i++;
        }

        for(i=0;i<tmpevents.size();i++){
            holidaysEventsInfo.put(tmpevents.get(i),tmpeventsInfo.get(i));
        }

    }


    private void setHolidays() {
        holidays.put("January", 2);
        holidays.put("February", 3);
        holidays.put("March", 20);
        holidays.put("June", 18);
        holidays.put("July", 22);
        holidays.put("July", 30);
        holidays.put("December", 10);
        holidays.put("November", 28);
    }


    private void setCalender(){
        month.setText(months.get(currentMonth));
        Boolean endloop = false; //check if total no of days in a month exceeded

        int k = 1; //VALUE OF A DAY

        // ADD 1st ROW TO THE TABLE
        l = (LayoutInflater) getApplicationContext().getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
        rowview = l.inflate(R.layout.row_layout,null);
        mainTable.addView(rowview);
        k = setStartfMonth(); //display cells in the 1st row

        for(int i=0;i<5;i++){
            // ADD ROW TO THE TABLE
            addRow();

            for(int j=0;j<7;j++){
                //ADD CELL TO THE ROW
                l = (LayoutInflater) getApplicationContext().getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
                View cellview = l.inflate(R.layout.calender_cell,null);
                TextView cellItem = (TextView) cellview.findViewById(R.id.cell);
                cellItem.setText(""+k);
                cellview.setId(k);
                k++;
                TableRow row = (TableRow) rowview.findViewById(R.id.table_row);
                row.addView(cellview);

                //DISPLAY INFO IF CLICKED ON CELL
                setListener(cellview);

                //CHANGE CELL COLOR IF ITS A HOLIDAY
                checkIfEvent(cellItem,k-1);

                //BREAK THE LOOP IF DAY EXCEEDS VALUE FROM DAYS ARRAYLIST
                if(k>days.get(currentMonth)){
                    endloop = true;
                    break;
                }
            }
            //Terminate loop
            if(endloop){
                break;
            }
        }

    }


    private void addRow() {
        l = (LayoutInflater) getApplicationContext().getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
        rowview = l.inflate(R.layout.row_layout,null);
        mainTable.addView(rowview);
    }


    private int setStartfMonth() {
        int k = 1;
        //Display blanks for the first n cells
        for(int i=0;i<startofMonth.get(currentMonth);i++){
            l = (LayoutInflater) getApplicationContext().getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
            View cellview = l.inflate(R.layout.calender_cell,null);
            TextView cellItem = (TextView) cellview.findViewById(R.id.cell);
            cellItem.setText("");
            TableRow row = (TableRow) rowview.findViewById(R.id.table_row);
            row.addView(cellview);
        }
        //start displaying days from 1
        for(int i=0;i<(7-startofMonth.get(currentMonth));i++){
            l = (LayoutInflater) getApplicationContext().getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
            View cellview = l.inflate(R.layout.calender_cell,null);
            TextView cellItem = (TextView) cellview.findViewById(R.id.cell);
            cellItem.setText(""+k);
            TableRow row = (TableRow) rowview.findViewById(R.id.table_row);
            row.addView(cellview);
            cellview.setId(k);

            //DISPLAY INFO IF CLICKED ON CELL
            setListener(cellview);

            //CHANGE CELL COLOR IF ITS A HOLIDAY
            checkIfEvent(cellItem,k);
            k++;


        }
        return k;
    }

    //Set onclick listener to each cell
    private void setListener(View cellview) {
        cellview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoDetails.setVisibility(View.GONE);
                infoLayout = (LinearLayout)findViewById(R.id.info_layout);
                infoLayout.setVisibility(View.VISIBLE);
                int id=view.getId();

                String key = months.get(currentMonth)+id;
                //Compare key with holidaysEvent key
                if(holidaysEvents.containsKey(key)){
                    infoTitle.setText(holidaysEvents.get(key));
                }else{
                    infoTitle.setText("No holiday");
                }

            }
        });
    }

    //Check if there is an event on particular day
    private void checkIfEvent(TextView cellItem,int k) {
        if((holidays.containsKey(months.get(currentMonth)))&&((k)==holidays.get(months.get(currentMonth)))){
            cellItem.setBackgroundColor(Color.BLUE);
        }
    }


    @Override
    public void onClick(View view) {
        //next month
        if(view.getId()==R.id.leftbtn){
            currentMonth--;
            if(currentMonth==-1){
                currentMonth=11;
            }
            mainTable.removeAllViews();
            setCalender();

        }if(view.getId()==R.id.rightbtn){ //Previous month
            currentMonth++;
            if(currentMonth==12){
                currentMonth=0;
            }
            mainTable.removeAllViews();
            setCalender();
        }if(view.getId()==R.id.info_title){
            //If there is an event on day show info about event
            if(holidaysEventsInfo.containsKey(infoTitle.getText().toString())){
                if(infoDetails.getVisibility()==View.GONE){
                    infoDetails.setVisibility(View.VISIBLE);
                }else{
                    infoDetails.setVisibility(View.GONE);
                }
                String txt = infoTitle.getText().toString();
                infoDetails.setText(holidaysEventsInfo.get(txt));
            }

        }
    }
}