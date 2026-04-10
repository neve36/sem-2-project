import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

// Participant Class
class Participant implements Serializable {
    int id;
    String name, college, event, contact;

    Participant(int id, String name, String college, String event, String contact) {
        this.id = id;
        this.name = name;
        this.college = college;
        this.event = event;
        this.contact = contact;
    }
}

public class EventGUI extends JFrame {

    JTextField txtId, txtName, txtCollege, txtEvent, txtContact, txtSearch;
    JTable table;
    DefaultTableModel model;

    ArrayList<Participant> list = new ArrayList<>();
    final String FILE = "data.dat";

    public EventGUI() {

        setTitle("Event Registration System");
        setSize(900,600);

        Color bg = new Color(30,30,30);
        Color panel = new Color(45,45,45);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(bg);
        main.setBorder(BorderFactory.createEmptyBorder(20,40,20,40));

        // FORM
        JPanel form = new JPanel(new GridLayout(6,2,15,15));
        form.setBackground(panel);

        txtId = new JTextField();
        txtName = new JTextField();
        txtCollege = new JTextField();
        txtEvent = new JTextField();
        txtContact = new JTextField();

        String[] labelsText = {"Participant ID:", "Name:", "College:", "Event:", "Contact No:"};
        JTextField[] fields = {txtId, txtName, txtCollege, txtEvent, txtContact};

        for(int i=0;i<labelsText.length;i++){
            JLabel lbl = new JLabel(labelsText[i]);
            lbl.setForeground(Color.WHITE);
            form.add(lbl);
            form.add(fields[i]);
        }

        JButton add = new JButton("Register");
        add.setBackground(new Color(0,150,136));
        add.setForeground(Color.WHITE);

        JButton clear = new JButton("Clear");
        clear.setBackground(new Color(244,67,54));
        clear.setForeground(Color.WHITE);

        form.add(add); form.add(clear);

        // TABLE
        model = new DefaultTableModel(
                new String[]{"ID","Name","College","Event","Contact"},0);
        table = new JTable(model);

        table.setSelectionBackground(Color.YELLOW); // highlight

        // Zebra rows
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int c){
                Component comp = super.getTableCellRendererComponent(t,v,s,f,r,c);
                if(!s){
                    comp.setBackground(r%2==0 ? new Color(230,230,230) : Color.WHITE);
                }
                return comp;
            }
        });

        JScrollPane sp = new JScrollPane(table);

        // BOTTOM PANEL
        JPanel bottom = new JPanel();
        bottom.setBackground(panel);

        txtSearch = new JTextField(10);

        JButton search = new JButton("Search");
        search.setBackground(new Color(33,150,243));
        search.setForeground(Color.WHITE);

        JButton delete = new JButton("Delete");
        delete.setBackground(new Color(255,87,34));
        delete.setForeground(Color.WHITE);

        JButton update = new JButton("Update");
        update.setBackground(new Color(156,39,176));
        update.setForeground(Color.WHITE);

        JButton dashboard = new JButton("Dashboard");
        dashboard.setBackground(new Color(255,193,7));

        JButton filter = new JButton("View by Event");
        filter.setBackground(new Color(76,175,80));

        JButton showAll = new JButton("Show All"); // ✅ FIX BUTTON
        showAll.setBackground(new Color(120,120,120));
        showAll.setForeground(Color.WHITE);

        bottom.add(new JLabel("Search:"));
        bottom.add(txtSearch);
        bottom.add(search);
        bottom.add(delete);
        bottom.add(update);
        bottom.add(dashboard);
        bottom.add(filter);
        bottom.add(showAll);

        main.add(form,BorderLayout.NORTH);
        main.add(sp,BorderLayout.CENTER);
        main.add(bottom,BorderLayout.SOUTH);

        add(main);

        load();
        refresh();

        // CLICK → EDIT
        table.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
                int r = table.getSelectedRow();
                txtId.setText(model.getValueAt(r,0).toString());
                txtName.setText(model.getValueAt(r,1).toString());
                txtCollege.setText(model.getValueAt(r,2).toString());
                txtEvent.setText(model.getValueAt(r,3).toString());
                txtContact.setText(model.getValueAt(r,4).toString());
            }
        });

        // ACTIONS
        add.addActionListener(e -> addParticipant());
        clear.addActionListener(e -> clearFields());
        search.addActionListener(e -> search());
        delete.addActionListener(e -> delete());
        update.addActionListener(e -> update());
        dashboard.addActionListener(e -> showDashboard());
        filter.addActionListener(e -> viewByEvent());
        showAll.addActionListener(e -> refresh()); // ✅ FIX

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    // ADD
    void addParticipant(){
        try{
            String idText = txtId.getText();
            String contact = txtContact.getText();

            if(!idText.matches("\\d+")){
                JOptionPane.showMessageDialog(this,"ID must be number!");
                return;
            }

            if(!contact.matches("\\d{10}")){
   		 JOptionPane.showMessageDialog(this,"Contact must be exactly 10 digits!");
   		 return;
            }

            int id = Integer.parseInt(idText);

            list.add(new Participant(id,
                    txtName.getText(),
                    txtCollege.getText(),
                    txtEvent.getText(),
                    contact));

            save(); refresh(); clearFields();

        }catch(Exception e){
            JOptionPane.showMessageDialog(this,"Error!");
        }
    }

    void refresh(){
        model.setRowCount(0);
        for(Participant p:list){
            model.addRow(new Object[]{p.id,p.name,p.college,p.event,p.contact});
        }
    }

    void clearFields(){
        txtId.setText(""); txtName.setText("");
        txtCollege.setText(""); txtEvent.setText("");
        txtContact.setText("");
    }

    // SEARCH
    void search(){
        String input = txtSearch.getText();

        for(int i=0;i<model.getRowCount();i++){
            if(model.getValueAt(i,0).toString().equals(input) ||
               model.getValueAt(i,1).toString().equalsIgnoreCase(input)){

                table.setRowSelectionInterval(i,i);
                table.scrollRectToVisible(table.getCellRect(i,0,true));
                return;
            }
        }

        JOptionPane.showMessageDialog(this,"Not Found");
    }

    void delete(){
        int id = Integer.parseInt(txtSearch.getText());
        list.removeIf(p -> p.id==id);
        save(); refresh();
    }

    void update(){
        int id = Integer.parseInt(txtId.getText());
        for(Participant p:list){
            if(p.id==id){
                p.name=txtName.getText();
                p.college=txtCollege.getText();
                p.event=txtEvent.getText();
                p.contact=txtContact.getText();
            }
        }
        save(); refresh();
    }

    // DASHBOARD
    void showDashboard(){
        HashMap<String,Integer> map = new HashMap<>();

        for(Participant p:list){
            map.put(p.event, map.getOrDefault(p.event,0)+1);
        }

        String msg = "Total Participants: "+list.size()+"\n";
        msg += "Total Events: "+map.size()+"\n\n";

        for(String e:map.keySet()){
            msg += e+" : "+map.get(e)+"\n";
        }

        JOptionPane.showMessageDialog(this,msg);
    }

    // FILTER
    void viewByEvent(){
        String eventName = JOptionPane.showInputDialog("Enter Event Name:");

        refresh(); // ✅ FIX
        model.setRowCount(0);

        for(Participant p:list){
            if(p.event.equalsIgnoreCase(eventName)){
                model.addRow(new Object[]{p.id,p.name,p.college,p.event,p.contact});
            }
        }
    }

    void save(){
        try{
            ObjectOutputStream o=new ObjectOutputStream(new FileOutputStream(FILE));
            o.writeObject(list);
            o.close();
        }catch(Exception e){}
    }

    void load(){
        try{
            ObjectInputStream i=new ObjectInputStream(new FileInputStream(FILE));
            list=(ArrayList<Participant>)i.readObject();
            i.close();
        }catch(Exception e){}
    }

    public static void main(String[] args){
        new EventGUI();
    }
}