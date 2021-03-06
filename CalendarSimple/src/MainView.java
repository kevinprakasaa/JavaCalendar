import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;

/**
 * MainView creates a view for a Calendar with a month view on the left
 * and a day view on the right.  Previous and Next buttons move the day by one
 * and update the month or year as necessary.  The current month and year are
 * displayed above the calendar while the current say is highlighted by a border.
 * the Create button opens a new view that allows the user to add a new event.
 * This event is added to the day view of the day selected.
 * @author sheff
 */
public class MainView {
    
    /**
     * Constructs a new Main View using an EventModel data model.
     * @param model is required to produce the correct data for the view.
     */
    public MainView(final EventModel model) {
        //Initializes model variable
        this.model = model;
        this.cal = model.getCal();
        
        //Initialazes and setsup buttons
        JButton createButton = new JButton("Create");
        JButton previousButton = new JButton("<");
        JButton nextButton = new JButton(">");
        
        /*
         * CONTROLLER used for main view.  This changes the model by
         * adding or subtracting a day or open a create event view.
         */
        createButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                CreateEventView cev = new CreateEventView(model);
            }
        });
        
        previousButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                model.previousDay();
            }
        });
        
        nextButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                model.nextDay();
            }
        });
        
        //Adds buttons to button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(createButton);
        buttonPanel.add(previousButton);
        buttonPanel.add(nextButton);
        
        //Sets up month panel and calls drawMonth to fill in initial data
        monthPanel = new JPanel();
        monthPanel.setLayout(new GridLayout(0, 7, 5, 5));
        monthPanel.setBorder(new EmptyBorder(0, 10, 0, 0));
        JPanel monthWrap = new JPanel();
        monthWrap.setLayout(new BoxLayout(monthWrap, BoxLayout.Y_AXIS));
        monthWrap.add(monthLabel);
        monthWrap.add(monthPanel);
        drawMonth(monthPanel);
        
        //Sets up day view and puts in a scroll pane
        JScrollPane scroll = new JScrollPane();
        dayPanel = new JPanel();
        dayPanel.setLayout(new BoxLayout(dayPanel, BoxLayout.PAGE_AXIS));
        dayPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        drawDay(dayPanel);
        scroll.getViewport().add(dayPanel);
        scroll.setPreferredSize(new Dimension(200, 200));
        scroll.setVerticalScrollBarPolicy(ScrollPaneLayout.VERTICAL_SCROLLBAR_ALWAYS);
       
        //Adds all panels to frame and sets up frame
        JFrame frame = new JFrame();
        frame.add(buttonPanel, BorderLayout.NORTH);
        frame.add(monthWrap, BorderLayout.WEST);
        frame.add(scroll, BorderLayout.EAST);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
    
    /**
     * This forces the repainting of all variable items in main view.
     * It starts by removing all previous items then redrawing with updated data
     */
    public void repaint() {
        monthPanel.removeAll();
        drawMonth(monthPanel);
        monthPanel.revalidate();
        monthPanel.repaint();
        
        dayPanel.removeAll();
        drawDay(dayPanel);
        dayPanel.revalidate();
        dayPanel.repaint();
    }

    /**
     * Takes a panel and populates it with the month set in the data model.
     * @param monthPanel 
     */
    private void drawMonth(JPanel monthPanel) {
        
        monthLabel.setText(new SimpleDateFormat("MMM yyyy").format(cal.getTime()));
        
        //Add Week Labels at top of Month View
        String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i<7; i++) {
            JLabel day = new JLabel("<html><u>" + daysOfWeek[i] + "</u></html>");
            monthPanel.add(day);
        }
        
        //Add days in month
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        Calendar getStart = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1);
        int startDay = getStart.get(Calendar.DAY_OF_WEEK);
        
        for (int i = 1; i<daysInMonth+startDay; i++) {
            if (i<startDay) {
                final JLabel day = new JLabel("");
                
                monthPanel.add(day);
            } else {
                int dayNumber = i-startDay+1;
                final JLabel day = new JLabel(dayNumber+"");
                day.addMouseListener(new MouseListener() {

                    //CONTROLLER updates the model on the currently looked day
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        int num = Integer.parseInt(day.getText());
                        model.setDay(num);
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {}

                    @Override
                    public void mouseReleased(MouseEvent e) {}

                    @Override
                    public void mouseEntered(MouseEvent e) {}

                    @Override
                    public void mouseExited(MouseEvent e) {}
                });
                if (dayNumber == cal.get(Calendar.DAY_OF_MONTH)) {
                    day.setBorder(BorderFactory.createLineBorder(Color.blue));
                }
                monthPanel.add(day);
            }
        }
    }
    
    /**
     * Populates the day panel with events that have been added to the data model
     */
    private void drawDay(JPanel dayPanel) {
        
        ArrayList<Event> todaysEvents = model.getEvents();
        
        for (Event e : todaysEvents) {
            if (e.start.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH)) {
                
                Date startDate = e.start.getTime();
                Date endDate = e.end.getTime();
                
                SimpleDateFormat sf = new SimpleDateFormat("hh:mm aa");
                
                dayPanel.add(new JLabel(e.name));
                dayPanel.add(new JLabel(sf.format(startDate)));
                dayPanel.add(new JLabel(sf.format(endDate)));
            }
        }
    }

    private EventModel model;
    private final Calendar cal;
    private final JLabel monthLabel = new JLabel();
    private final JLabel dayLabel = new JLabel();
    private final JPanel monthPanel;
    private final JPanel dayPanel;

}
