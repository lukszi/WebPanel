package gui.view;

import dataModel.entities.DisplayGroup;
import dataModel.entities.DisplayInterval;
import gui.util.InvalidDisplayGroupException;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import network.Connection;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

/**
 * Created by Lukas Szimtenings on 09.01.2017.
 * A windows that let's you edit a DisplayGroup or create a new one.
 */
class DisplayGroupConfiguratorGui
{
    //Becomes true once the configuration is done
    //When false it is assumed that the configuration is invalid
    private boolean correct = false;
    private Map<Integer,DisplayGroup> displayGroupMap;
    private DisplayGroup currentDisplayGroup;
    private DisplayInterval currentInterval;
    
    private Stage editWindow;
    private Scene editScene;
    
    ChoiceBox<Integer> cbGroups;
    private Button cmdCreateGroup;
    private Button cmdOk;
    private Button cmdCancel;
    private Label lblId;
    
    //Controls that are contained in the pagination boxes
    private Pagination pagination;
    private Button cmdAddInterval;
    private Button cmdRemoveInterval;
    private TextField txtStartHour;
    private TextField txtStartMinute;
    private TextField txtEndHour;
    private TextField txtEndMinute;
    private ToggleGroup tglDaysOfTheWeek;
    private RadioButton rdMonday;
    private RadioButton rdTuesday;
    private RadioButton rdWednesday;
    private RadioButton rdThursday;
    private RadioButton rdFriday;
    private RadioButton rdSaturday;
    private RadioButton rdSunday;
    
    /**
     * creates a window to edit or create a new DisplayGroup
     * @param displayGroup DisplayGroup that should be edited, if null, a random group is chosen
     */
    DisplayGroupConfiguratorGui(DisplayGroup displayGroup)
    {
        displayGroupMap = Connection.getConnection().getGroupMap().groupMap;
        //make sure we have a valid DisplayGroup
        if(displayGroup != null)
        {
            //Valid DisplayGroup passed
            this.currentDisplayGroup = displayGroup;
        }
        else if(displayGroupMap.size()!=0)
        {
            //DisplayGroupMap not empty, select a random group
            this.currentDisplayGroup = displayGroupMap.get(displayGroupMap.keySet().iterator().next());
        }
        else
        {
            //DisplayGroupMap empty, generate a new group
            this.currentDisplayGroup = createNewDisplayGroup();
        }
        
        //initialize the Window
        editWindow = new Stage();
        
        initEditScene();
        editWindow.setTitle("Edit DisplayGroup Configuration");
        editWindow.setScene(editScene);
        editWindow.showAndWait();
    }
    
    /**
     * initializes all ui Elements, first method that should be called
     */
    private void initEditScene()
    {
        //Init main panel
        GridPane editDisplayGroupPane = new GridPane();
        
        //Init buttons
        initButtons();
        editDisplayGroupPane.add(cmdOk,1,3);
        editDisplayGroupPane.add(cmdCancel,2,3);
        
        //Init RadioButtons for interval selection
        tglDaysOfTheWeek = new ToggleGroup();
        rdMonday = new RadioButton("Monday");
        rdMonday.setToggleGroup(tglDaysOfTheWeek);
        rdMonday.setUserData(1);
        rdTuesday = new RadioButton("Tuesday");
        rdTuesday.setToggleGroup(tglDaysOfTheWeek);
        rdTuesday.setUserData(2);
        rdWednesday = new RadioButton("Wednesday");
        rdWednesday.setToggleGroup(tglDaysOfTheWeek);
        rdWednesday.setUserData(3);
        rdThursday = new RadioButton("Thursday");
        rdThursday.setToggleGroup(tglDaysOfTheWeek);
        rdThursday.setUserData(4);
        rdFriday = new RadioButton("Friday");
        rdFriday.setToggleGroup(tglDaysOfTheWeek);
        rdFriday.setUserData(5);
        rdSaturday = new RadioButton("Saturday");
        rdSaturday.setToggleGroup(tglDaysOfTheWeek);
        rdSaturday.setUserData(6);
        rdSunday = new RadioButton("Sunday");
        rdSunday.setToggleGroup(tglDaysOfTheWeek);
        rdSunday.setUserData(7);
                    
        lblId = new Label();
        //init ChoiceBox to choose DisplayGroup from
        cbGroups = new ChoiceBox<>(FXCollections.observableArrayList(displayGroupMap.keySet()));
        //Add listener that updates the pagination when DisplayGroup is chosen
        cbGroups.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                {
                    updatePagination(displayGroupMap.get(newValue));
                    lblId.setText(Integer.toString(currentDisplayGroup.getGroupID()));
                });
        //Add ChoiceBox row with GroupIdLabel and createGroup button in first row
        editDisplayGroupPane.add(cbGroups,1,1);
        editDisplayGroupPane.add(cmdCreateGroup,3,1);
        editDisplayGroupPane.add(lblId,2,1);
        
        //Init pagination and add it in second row
        pagination = new Pagination();
        pagination.setPageFactory(this::createPage);
        cbGroups.getSelectionModel().select(0);
        //updatePagination(currentDisplayGroup);
        GridPane.setColumnSpan(pagination,3);
        editDisplayGroupPane.add(pagination,1,2);
        
        editDisplayGroupPane.setPadding(new Insets(10,10,10,10));
        editDisplayGroupPane.setVgap(8);
        editDisplayGroupPane.setHgap(10);
        
        editScene = new Scene(editDisplayGroupPane);
        editScene.getStylesheets().add(getClass().getClassLoader().getResource("text-field-error.css").toExternalForm());
    }
    
    /**
     * Checks currentDisplayGroup for logical errors. Throws Exception with detailed description if something went wrong
     * @return true if everything is alright
     * @throws InvalidDisplayGroupException Thrown if there is something wrong, contains details as to what went wrong
     */
    private boolean checkInput() throws InvalidDisplayGroupException
    {
        if(currentDisplayGroup.getIntervals().isEmpty())
        {
            //User didn't add any intervals
            InvalidDisplayGroupException e = new InvalidDisplayGroupException();
            e.setErrorType(InvalidDisplayGroupException.ErrorType.NOINTERVALL);
            throw e;
        }
        else
        {
            //Check each DisplayInterval
            for (DisplayInterval interval : currentDisplayGroup.getIntervals())
            {
                if(interval.getDisplayStart()==null)
                {
                    //DisplayStart value is invalid
                    InvalidDisplayGroupException e = new InvalidDisplayGroupException();
                    e.setErrorType(InvalidDisplayGroupException.ErrorType.STARTTIMENOTSET);
                    e.setWrongInterval(interval);
                    throw e;
                }
                if(interval.getDisplayStop()==null)
                {
                    //DisplayStop value is invalid
                    InvalidDisplayGroupException e = new InvalidDisplayGroupException();
                    e.setErrorType(InvalidDisplayGroupException.ErrorType.ENDTIMENOTSET);
                    e.setWrongInterval(interval);
                    throw e;
                }
                if(interval.getDisplayStart().isAfter(interval.getDisplayStop()))
                {
                    //Display is supposed to stop before it starts
                    InvalidDisplayGroupException e = new InvalidDisplayGroupException();
                    e.setErrorType(InvalidDisplayGroupException.ErrorType.INTERVALINVALID);
                    e.setWrongInterval(interval);
                    throw e;
                }
            }
        }
        return true;
    }
    
    /**
     * Updates the content of the pagination to show the Intervals in the passed group
     * @param group The group the pagination is supposed to show
     */
    private void updatePagination(DisplayGroup group)
    {
        updatePagination(group,null);
    }
    
    /**
     * Updates the content of the pagination to show the Intervals in the passed group
     * @param group The group the pagination is supposed to show
     * @param interval The interval that should be shown after the update
     */
    private void updatePagination(DisplayGroup group, DisplayInterval interval)
    {
        //Get the corresponding DisplayGroup
        currentDisplayGroup = group;
        pagination.setPageCount(currentDisplayGroup.getIntervals().size());
        //Set page to be shown
        if(interval!=null)
        {
            pagination.setCurrentPageIndex(currentDisplayGroup.getIntervals().indexOf(interval));
            currentInterval = interval;
        }
        else
        {
            pagination.setCurrentPageIndex(0);
            currentInterval = currentDisplayGroup.getIntervals().get(0);
        }
    }
    
    /**
     * Callback function for the pagination generates the display page for the displayInterval,
     * sitting in the interval list of the currentDisplayGroup at pageIndex position.
     * Also saves the previous values.
     * @param pageIndex Index of the page that should be created
     * @return returns
     */
    private VBox createPage(int pageIndex)
    {
        //Save values from previous interval page
        if(currentInterval != null && txtStartHour!=null)
        {
            try
            {
                //Parse times from TextFields
                LocalTime startTime = null;
                LocalTime endTime = null;
                
                //Check if TextFields are proper before parsing them
                if(txtStartHour.getText().length()!=0&&txtStartMinute.getText().length()!=0)
                {
                    startTime = LocalTime.of(Integer.parseInt(txtStartHour.getText()),Integer.parseInt(txtStartMinute.getText()));
                }
                                 
                if(txtEndHour.getText().length()!=0&&txtEndMinute.getText().length()!=0)
                {
                    endTime = LocalTime.of(Integer.parseInt(txtEndHour.getText()),Integer.parseInt(txtEndMinute.getText()));
                }
                
                //Extract selected active day
                if(tglDaysOfTheWeek.getSelectedToggle()!=null && startTime != null && endTime != null)
                {
                    //Active day is set, we can save this interval
                    int activeDay = (Integer)tglDaysOfTheWeek.getSelectedToggle().getUserData();
                    currentInterval.setDisplayStart(startTime);
                    currentInterval.setDisplayStop(endTime);
                    currentInterval.setActiveDay(activeDay);
                }
            }
            catch (DateTimeParseException e)
            {
                e.printStackTrace();
            }
        }
        
        //Start creating the new page
        currentInterval = currentDisplayGroup.getIntervals().get(pageIndex);
        
        VBox page = new VBox();
        //Add the radios
        page.getChildren().add(rdMonday);
        page.getChildren().add(rdTuesday);
        page.getChildren().add(rdWednesday);
        page.getChildren().add(rdThursday);
        page.getChildren().add(rdFriday);
        page.getChildren().add(rdSaturday);
        page.getChildren().add(rdSunday);
        
        //Select previously selected RadioButton
        RadioButton rdActive = null;
        if(currentInterval.getActiveDay()!=null)
        {
            for(Toggle tgl : tglDaysOfTheWeek.getToggles())
            {
                if(tgl.getUserData().equals(currentInterval.getActiveDay()))
                {
                    rdActive = (RadioButton)tgl;
                    break;
                }
            }
        }
        //TODO: Check if the above cast from toggle to RadioButton works and get rid of this skeleton
        /*switch (currentInterval.getActiveDay())
        {
            case 1:
                rdActive = rdMonday;
                break;
            case 2:
                rdActive = rdTuesday;
                break;
            case 3:
                rdActive = rdWednesday;
                break;
            case 4:
                rdActive = rdThursday;
                break;
            case 5:
                rdActive = rdFriday;
                break;
            case 6:
                rdActive = rdSaturday;
                break;
            case 7:
                rdActive = rdSunday;
                break;
            default:
                rdActive = rdMonday;
        }*/
        if(rdActive != null)
        {
            rdActive.setSelected(true);
            rdActive.requestFocus();
        }
        
        //Init new TextFields
        HBox startTimeBox = new HBox();
        HBox endTimeBox = new HBox();
        HBox ctrlBox = new HBox();
        txtStartHour = new TextField();
        txtStartMinute = new TextField();
        txtEndHour = new TextField();
        txtEndMinute = new TextField();
        if(currentInterval.getDisplayStart()!=null)
        {
            //Set previously entered values
            txtStartHour.setText(Integer.toString(currentInterval.getDisplayStart().getHour()));
            txtStartMinute.setText(Integer.toString(currentInterval.getDisplayStart().getMinute()));
        }
        if(currentInterval.getDisplayStop()!=null)
        {
            txtEndHour.setText(Integer.toString(currentInterval.getDisplayStop().getHour()));
            txtEndMinute.setText(Integer.toString(currentInterval.getDisplayStop().getMinute()));
        }
        //Add TextFields to corresponding VBoxes
        startTimeBox.getChildren().add(txtStartHour);
        startTimeBox.getChildren().add(txtStartMinute);
        endTimeBox.getChildren().add(txtEndHour);
        endTimeBox.getChildren().add(txtEndMinute);
        //Add controls to box
        ctrlBox.getChildren().add(cmdAddInterval);
        ctrlBox.getChildren().add(cmdRemoveInterval);
        //Add all boxes to the page
        page.getChildren().add(startTimeBox);
        page.getChildren().add(endTimeBox);
        page.getChildren().add(ctrlBox);
        return page;
    }
    
    /**
     * creates a new DisplayGroup , with an empty interval, and puts it in the currently loaded displayGroupMap
     * @return freshly generated currentDisplayGroup
     */
    private DisplayGroup createNewDisplayGroup()
    {
        DisplayGroup freshGroup = new DisplayGroup();
        //Find a key that's not taken
        Object[] existingKeys = displayGroupMap.keySet().toArray();
        int freshKey = 1;
        if(existingKeys.length != 0)
        {
            //DisplayGroup list not empty, find biggest existing key
            freshKey = (Integer)existingKeys[0];
            for (Object key : existingKeys)
            {
                if ((Integer)key > freshKey)
                {
                    freshKey = (Integer)key;
                }
            }
            //Make new key one bigger than biggest currently existing key
            freshKey++;
        }
        freshGroup.setGroupID(freshKey);
        System.out.println("Created new group with ID: " + freshKey);
        //Create a new interval with standard values
        DisplayInterval interval = new DisplayInterval();
        interval.setActiveDay(1);
        freshGroup.getIntervals().add(interval);
        //Put new DisplayGroup into the map
        displayGroupMap.put(freshKey,freshGroup);
        return freshGroup;
    }
    
    private void initButtons()
    {
        cmdOk = new Button("Save");
        cmdOk.setOnAction(event ->
        {
            try
            {
                //Check configuration before closing
                checkInput();
                correct = true;
                editWindow.close();
            }
            catch (InvalidDisplayGroupException e)
            {
                //Configuration is wrong, start error handling
                switch (e.getErrorType())
                {
                    case NOINTERVALL:
                    {
                        //No interval Configured, alert user to that
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid configuration");
                        alert.setHeaderText("No interval configured");
                        alert.setContentText("Please add an Interval to the DisplayGroup");
                        alert.showAndWait();
                        break;
                    }
                    case INTERVALINVALID:
                    {
                        //User configured interval so that it would stop before it would start
                        //Change pagination to show wrong interval
                        DisplayInterval wrong = e.getWrongInterval();
                        int index = currentDisplayGroup.getIntervals().indexOf(wrong);
                        pagination.setCurrentPageIndex(index);
                        //Style time fields as error
                        txtStartHour.getStyleClass().add("error");
                        txtStartMinute.getStyleClass().add("error");
                        txtEndHour.getStyleClass().add("error");
                        txtEndMinute.getStyleClass().add("error");
                        break;
                    }
                    case ENDTIMENOTSET:
                    {
                        //User forgot to enter stop times
                        //Change pagination to show wrong interval
                        DisplayInterval wrong = e.getWrongInterval();
                        pagination.setCurrentPageIndex(currentDisplayGroup.getIntervals().indexOf(wrong));
                        //Style stop time fields as error
                        txtEndHour.getStyleClass().add("error");
                        txtEndMinute.getStyleClass().add("error");
                        break;
                    }
                    case STARTTIMENOTSET:
                    {
                        //User forgot to enter start times
                        //Change pagination to show wrong interval
                        DisplayInterval wrong = e.getWrongInterval();
                        int index = currentDisplayGroup.getIntervals().indexOf(wrong);
                        pagination.setCurrentPageIndex(index);
                        //Style start time fields as error
                        txtStartHour.getStyleClass().add("error");
                        txtStartMinute.getStyleClass().add("error");
                        break;
                    }
                }
            }
        });
        
        cmdCancel = new Button("Cancel");
        cmdCancel.setOnAction(event ->
        {
            //Closes the window and declares the results invalid
            correct = false;
            editWindow.close();
        });
        
        cmdAddInterval = new Button("Add Interval");
        cmdAddInterval.setOnAction(event ->
        {
            //Create new interval and update pagination
            DisplayInterval interval = new DisplayInterval();
            currentDisplayGroup.getIntervals().add(interval);
            updatePagination(currentDisplayGroup,interval);
        });
        cmdRemoveInterval = new Button("Remove this Interval");
        cmdRemoveInterval.setOnAction(event ->
        {
            if(currentDisplayGroup.getIntervals().size()<=1)
            {
                //TODO: give the user a message or something telling him that he can't delete the last interval
                return;
            }
            int index = currentDisplayGroup.getIntervals().indexOf(currentInterval);
            currentDisplayGroup.getIntervals().remove(index);
            updatePagination(currentDisplayGroup);
        });
        cmdCreateGroup = new Button("New Group");
        cmdCreateGroup.setOnAction(event ->
        {
            DisplayGroup grp = createNewDisplayGroup();
            cbGroups.getItems().add(grp.getGroupID());
            //TODO: Find out why the select call doesn't automatically select the new group
            cbGroups.getSelectionModel().select(grp.getGroupID());
        } );
        
    }
    
    /**
     * @return DisplayGroup configured by the user, null if the user cancelled
     */
    DisplayGroup getDisplayGroup()
    {
        if(!correct)
            return null;
        return currentDisplayGroup;
    }
}
