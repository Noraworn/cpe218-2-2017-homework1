import java.util.Stack;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
 
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
 
import java.net.URL;
import java.io.IOException;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Scanner;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultTreeCellRenderer;

public class Homework1 {
    public static Node idiotTree;
    public static Stack<Character> EX = new Stack<Character>();
    
    public static void main(String[] args) {
            
            System.out.print("Input: ");
            Scanner input = new Scanner(System.in);
            String HW2 = input.nextLine();
            String example = HW2;
            for(int i = 0; i < example.length(); i++)
            {
                EX.push(example.charAt(i));
            }
            idiotTree = new Node(EX.pop());
            infix(idiotTree);
            System.out.print("Output: ");
            inorder(idiotTree);
            System.out.print("=");
            System.out.print(calculate(idiotTree));
                
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    GUITree.createAndShowGUI();
                }
            });
    }
        
        public static void infix(Node node)
        {
            if(node.symbol == '+' || node.symbol == '-' || node.symbol == '*' || node.symbol == '/')
            {
                node.right = new Node(EX.pop());
                infix(node.right);
                node.left = new Node(EX.pop());
                infix(node.left);
            }
        }
        
        public static void inorder(Node node)
        {
            if(node.symbol == '+' || node.symbol =='-' || node.symbol == '*' || node.symbol == '/' )
            {
                if(node != idiotTree)
                {
                    System.out.print("(");
                    inorder(node.left);
                    System.out.print(node.symbol);
                    inorder(node.right);
                    System.out.print(")");
                }
                else
                {
                    inorder(node.left);
                    System.out.print(node.symbol);
                    inorder(node.right);
                }
                
            }
            if(node.left == null && node.right == null)
            {
                System.out.print(node.symbol);
            }
            
        }
        
        public static int calculate(Node node)
        {
            switch(node.symbol)
            {
                case '+':
                    return calculate(node.left) + calculate(node.right);
                case '-':
                    return calculate(node.left) - calculate(node.right);
                case '*':
                    return calculate(node.left) * calculate(node.right);
                case '/':
                    return calculate(node.left) / calculate(node.right);
                default:
                    return Integer.parseInt(node.symbol.toString());
            }
        }
}

class Node
{
    public Character symbol;
    public Node left;
    public Node right;
    public Node parent;
    Node(char n)
    {
        symbol = n;
    }
    public String toString(){
        return symbol.toString();
    }
}

class GUITree extends JPanel
                      implements TreeSelectionListener {
    private JEditorPane htmlPane;
    private JTree guiTree;
    private URL helpURL;
    private static boolean DEBUG = false;
 
    //Optionally play with line styles.  Possible values are
    //"Angled" (the default), "Horizontal", and "None".
    private static boolean playWithLineStyle = false;
    private static String lineStyle = "Horizontal";
     
    //Optionally set the look and feel.
    private static boolean useSystemLookAndFeel = false;
 
    public GUITree() {
        super(new GridLayout(1,0));
 
        //Create the nodes.
        DefaultMutableTreeNode top =
            new DefaultMutableTreeNode(Homework1.idiotTree);
        createNodes(top,Homework1.idiotTree);
 
        //Create a tree that allows one selection at a time.
        guiTree = new JTree(top);
        guiTree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        //Listen for when the selection changes.
        guiTree.addTreeSelectionListener(this);
 
        if (playWithLineStyle) {
            System.out.println("line style = " + lineStyle);
            guiTree.putClientProperty("JTree.lineStyle", lineStyle);
        }
 
        //Create the scroll pane and add the tree to it. 
        JScrollPane treeView = new JScrollPane(guiTree);
 
        //Create the HTML viewing pane.
        htmlPane = new JEditorPane();
        htmlPane.setEditable(false);
        initHelp();
        JScrollPane htmlView = new JScrollPane(htmlPane);
 
        //Add the scroll panes to a split pane.
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(treeView);
        splitPane.setBottomComponent(htmlView);
 
        Dimension minimumSize = new Dimension(100, 50);
        htmlView.setMinimumSize(minimumSize);
        treeView.setMinimumSize(minimumSize);
        splitPane.setDividerLocation(100); 
        splitPane.setPreferredSize(new Dimension(500, 300));
 
        //Set the icon for leaf nodes.
        ImageIcon leafIcon = createImageIcon("middle.gif");
        if (leafIcon != null) {
            DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
            renderer.setClosedIcon(leafIcon);
            renderer.setOpenIcon(leafIcon);
            guiTree.setCellRenderer(renderer);
        }
        
        //Add the split pane to this panel.
        add(splitPane);
    }
 
    /** Required by TreeSelectionListener interface. */
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
        guiTree.getLastSelectedPathComponent();
 
        if (node == null) return;
 
        Object nodeInfo = node.getUserObject();
        if (node.isLeaf()) {
            BookInfo book = (BookInfo)nodeInfo;
            displayURL(book.bookURL);
            if (DEBUG) {
                System.out.print(book.bookURL + ":  \n    ");
            }
        } else {
            displayURL(helpURL); 
        }
        if (DEBUG) {
            System.out.println(nodeInfo.toString());
        }
    }
 
    private class BookInfo {
        public String bookName;
        public URL bookURL;
 
        public BookInfo(String book, String filename) {
            bookName = book;
            bookURL = getClass().getResource(filename);
            if (bookURL == null) {
                System.err.println("Couldn't find file: "
                                   + filename);
            }
        }
 
        public String toString() {
            return bookName;
        }
    }
 
    private void initHelp() {
        String s = "TreeDemoHelp.html";
        helpURL = getClass().getResource(s);
        if (helpURL == null) {
            System.err.println("Couldn't open help file: " + s);
        } else if (DEBUG) {
            System.out.println("Help URL is " + helpURL);
        }
 
        displayURL(helpURL);
    }
 
    private void displayURL(URL url) {
        try {
            if (url != null) {
                htmlPane.setPage(url);
            } else { //null url
        htmlPane.setText("File Not Found");
                if (DEBUG) {
                    System.out.println("Attempted to display a null URL.");
                }
            }
        } catch (IOException e) {
            System.err.println("Attempted to read a bad URL: " + url);
        }
    }
 
    private void createNodes(DefaultMutableTreeNode top, Node tree) {
        if(tree.left != null){
                 DefaultMutableTreeNode LNode = new DefaultMutableTreeNode(tree.left);
                 top.add(LNode);
                 createNodes(LNode,tree.left);
             }
             if(tree.right != null){
                 DefaultMutableTreeNode RNode = new DefaultMutableTreeNode(tree.right);
                 top.add(RNode);
                 createNodes(RNode,tree.right);
             }
    }
         
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    public static void createAndShowGUI() {
        if (useSystemLookAndFeel) {
            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Couldn't use system look and feel.");
            }
        }
 
        //Create and set up the window.
        JFrame frame = new JFrame("TreeDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Add content to the window.
        frame.add(new GUITree());
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = GUITree.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}
