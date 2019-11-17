package ai;

import ai.Global;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import kalaha.*;

/**
 * This is the main class for your Kalaha AI bot. Currently
 * it only makes a random, valid move each turn.
 * 
 * @author Johan Hagelbäck
 */
public class AIClient implements Runnable
{
    private int player;
    private JTextArea text;
    
    private PrintWriter out;
    private BufferedReader in;
    private Thread thr;
    private Socket socket;
    private boolean running;
    private boolean connected;
    	
    /**
     * Creates a new client.
     */
    public AIClient()
    {
	player = -1;
        connected = false;
        
        //This is some necessary client stuff. You don't need
        //to change anything here.
        initGUI();
	
        try
        {
            addText("Connecting to localhost:" + KalahaMain.port);
            socket = new Socket("localhost", KalahaMain.port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            addText("Done");
            connected = true;
        }
        catch (Exception ex)
        {
            addText("Unable to connect to server");
            return;
        }
    }
    
    /**
     * Starts the client thread.
     */
    public void start()
    {
        //Don't change this
        if (connected)
        {
            thr = new Thread(this);
            thr.start();
        }
    }
    
    /**
     * Creates the GUI.
     */
    private void initGUI()
    {
        //Client GUI stuff. You don't need to change this.
        JFrame frame = new JFrame("My AI Client");
        frame.setLocation(Global.getClientXpos(), 445);
        frame.setSize(new Dimension(420,250));
        frame.getContentPane().setLayout(new FlowLayout());
        
        text = new JTextArea();
        JScrollPane pane = new JScrollPane(text);
        pane.setPreferredSize(new Dimension(400, 210));
        
        frame.getContentPane().add(pane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.setVisible(true);
    }
    
    /**
     * Adds a text string to the GUI textarea.
     * 
     * @param txt The text to add
     */
    public void addText(String txt)
    {
        //Don't change this
        text.append(txt + "\n");
        text.setCaretPosition(text.getDocument().getLength());
    }
    
    /**
     * Thread for server communication. Checks when it is this
     * client's turn to make a move.
     */
    public void run()
    {
        String reply;
        running = true;
        
        try
        {
            while (running)
            {
                //Checks which player you are. No need to change this.
                if (player == -1)
                {
                    out.println(Commands.HELLO);
                    reply = in.readLine();

                    String tokens[] = reply.split(" ");
                    player = Integer.parseInt(tokens[1]);
                    
                    addText("I am player " + player);
                }
                
                //Check if game has ended. No need to change this.
                out.println(Commands.WINNER);
                reply = in.readLine();
                if(reply.equals("1") || reply.equals("2") )
                {
                    int w = Integer.parseInt(reply);
                    if (w == player)
                    {
                        addText("I won!");
                    }
                    else
                    {
                        addText("I lost...");
                    }
                    running = false;
                }
                if(reply.equals("0"))
                {
                    addText("Even game!");
                    running = false;
                }

                //Check if it is my turn. If so, do a move
                out.println(Commands.NEXT_PLAYER);
                reply = in.readLine();
                if (!reply.equals(Errors.GAME_NOT_FULL) && running)
                {
                    int nextPlayer = Integer.parseInt(reply);

                    if(nextPlayer == player)
                    {
                        out.println(Commands.BOARD);
                        String currentBoardStr = in.readLine();
                        boolean validMove = false;
                        while (!validMove)
                        {
                            long startT = System.currentTimeMillis();
                            //This is the call to the function for making a move.
                            //You only need to change the contents in the getMove()
                            //function.
                            GameState currentBoard = new GameState(currentBoardStr);
                            int cMove = getMove(currentBoard);
                            
                            //Timer stuff
                            long tot = System.currentTimeMillis() - startT;
                            double e = (double)tot / (double)1000;
                            
                            out.println(Commands.MOVE + " " + cMove + " " + player);
                            reply = in.readLine();
                            if (!reply.startsWith("ERROR"))
                            {
                                validMove = true;
                                addText("Made move " + cMove + " in " + e + " secs");
                            }
                        }
                    }
                }
                
                //Wait
                Thread.sleep(100);
            }
	}
        catch (Exception ex)
        {
            running = false;
        }
        
        try
        {
            socket.close();
            addText("Disconnected from server");
        }
        catch (Exception ex)
        {
            addText("Error closing connection: " + ex.getMessage());
        }
    }
    
    /**
     * This is the method that makes a move each time it is your turn.
     * Here you need to change the call to the random method to your
     * Minimax search.
     * 
     * @param currentBoard The current board state
     * @return Move to make (1-6)
     */
    public int getMove(GameState currentBoard)
    {

    	
    	int depth=1,result=0;
    	   int a[]=new int[14];
    		GameState gs=currentBoard.clone();
    	
    		int b[]=gs.getBoard();
    	
//    		int x=gs.getNextPlayer();
    		
    	  // array represents the current game state
    	    //int a[]={6,6,6,6,6,6,0,6,6,6,6,6,6,0};
    		for(int i=1;i<15;i++)
    		{	if(i==14)
    			a[13]=b[0];
    			else
    			a[i-1]=b[i];
    		System.out.println(a[i-1]);
    		}
    	    result=a[6]-a[13];
    	    if(player==1)
    	       depth=1;
    	     else
    	     {
    	    	 depth=1;
    	    	 for(int i=1;i<15;i++)
    	    	{	
    	    		 if(i>=1 && i<=6)
    	    		 a[i+7-1]=b[i];
    	    		 if(i>=8 && i<=13)
    	    		 a[i-7-1]=b[i];
    	    		 if(i==7)
    	    		 a[13]=b[7];
    	    		 if(i==14)
    	    		 a[6]=b[0];
    	    	}
    	   
    	     }
    	   
    	      kala(a,depth,result);
    	    // p is the best possible movie
    	    System.out.println(p+1);
    	   	
    
    	    return p+1;
    }
    
    
    static int p;
    static int kala(int a[],int depth,int result)
    {
        int[] a1 = new int[30];
        int[] b = new int[30];
        int i,j,k,count=0,count1=-1,max,min,sum=0,x=0,nn=0,sub=-1,count2;
        if(depth<=5)
        {
        // decides which player you are
            if(depth%2==1)
            {
                for(i=0;i<6;i++)
                {
                    if((i+a[i])==6)
                    {
                        count++;
                        for(k=0;k<=13;k++)
                        {
                            if(k>i && k<7)
                            {
                                 a1[k]=a[k]+1;
                            }
                            else
                            {
                          if(k!=i)
                           a1[k]=a[k];
                           else
                           a1[k]=0;
                            }
                        }
                        b[i]=kala(a1,depth,result+1);
                        nn=i;
                    }
                    else
                    {
                          if(a[i]!=0)
                          {  
                             count++;
                             count1=-1;
                             count2=0;
                             sub=-1;
                              if(a[i]>(12-i))
                              {
                                   count2=1;
                                   sub=(a[i]-(12-i));
                              }
                              for(j=0;j<=13;j++)
                              {  
                                  if(j<i)
                                     a1[j]=a[j];
                                  if(j>i && j<=(a[i]+i))    
                                  if(a[j]==0 && j==(a[i]+i) && j<6)
                                    {
                                        a1[j]=0;
                                        x=a[13-j-1]+1;
                                        a1[6]=a[6]+x;
                                        a1[13-j-1]=0;
                                        count1=13-j-1;
                                    }
                                    else
                                    if(j==6)
                                     {
                                         x=1;
                                         a1[j]=a[j]+1;
                                     }
                                     else
                                     {  
                                         if(j!=13)
                                         a1[j]=a[j]+1;
                                         else
                                         a1[j]=a[j];
                                     }
                                     if(j>(a[i]+i))
                                        if(count1!=j)
                                          a1[j]=a[j];
                                     if(j==i)
                                      a1[j]=0;
                                       
                              }
                              for(j=0;(j<sub && count2==1);j++)
                                 if(a[j]==0 && j==(sub-1) && j<6)
                                    {
                                        a1[j]=0;
                                        x=a[13-j-1]+1+x;
                                        a1[6]=a[6]+x;
                                        a1[13-j-1]=0;
                                        count1=13-j-1;
                                    }
                                    else
                                    if(j==6)
                                     {
                                         x=x+1;
                                         a1[j]=a[j]+1;
                                     }
                                     else
                                     {
                                         if(j!=13)
                                         a1[j]=a[j]+1;
                                         else
                                         a1[j]=a[j];
                                     }
                               
                               
                              b[i]=kala(a1,depth+1,result+x);
                              nn=i;
                              x=0;
                          }
                          else
                          {
                             
                              b[i]=1000;
                          }
                    }
                }
                if(count==0)
                {
                  //  for(i=0;i<6;i++)
                //        sum=sum+a[i];
                    return (result);
                }
                else
                {  
                    max=b[nn];
                    p=nn;
                    for(i=0;i<6;i++)
                      if(b[i]!=1000)
                      if(max<b[i])
                       {
                           max=b[i];
                           p=i;
                       }
                    return max;
                     
                }
            }
            else
            {
                for(i=7;i<13;i++)
                {
                    if((i+a[i])==13)
                    {
                        count++;
                        for(k=0;k<=13;k++)
                        {
                            if(k>i)
                            {
                                 a1[k]=a[k]+1;
                            }
                            else
                            {
                          if(k!=i)
                           a1[k]=a[k];
                           else
                           a1[k]=0;
                            }
                        }
                        b[i]=kala(a1,depth,result-1);
                        nn=i;
                    }
                    else
                    {
                        
                          if(a[i]!=0)
                          {  
                             count++;
                             count1=-1;
                             sub=-1;
                             count2=0;
                             x=0;
                             if(a[i]>(7+(12-i)))
                             {
                                 sub=(a[i]-(7+(12-i)));
                                 count2=1;
                             }
                             
                              for(j=7;j<=20;j++)
                              {  
                                  if(j<i)
                                     a1[j]=a[j];
                                  if(j>i && j<=(a[i]+i))    
                                  if(a[j]==0 && j==(a[i]+i) && j<13)
                                    {
                                        a1[j]=0;
                                        x=a[13-j-1]+1;
                                        a1[13]=a[13]+x;
                                        a1[13-j-1]=0;
                                        count1=13-j-1;
                                    }
                                    else
                                    if(j==13)
                                     {
                                         x=1;
                                         a1[j]=a[j]+1;
                                     }
                                     else
                                     {  
                                         if(j<13)
                                         a1[j]=a[j]+1;
                                         else
                                         {
                                         if(j!=20)
                                         a1[j-13-1]=a[j-13-1]+1;
                                         else
                                         a1[j-13-1]=a[j-13-1];
                                         }
                                     }
                                     if(j>(a[i]+i))
                                        if(j<=13)
                                        a1[j]=a[j];
                                        else
                                        if(count1!=(j-13-1))
                                          a1[j-13-1]=a[j-13-1];
                                     if(j==i)
                                      a1[j]=0;
                                       
                              }
                              for(j=7;(j<(7+sub) && count2==1);j++)
                                 if(a[j]==0 && j==(7+sub-1) && j<13)
                                    {
                                        a1[j]=0;
                                        x=a[13-j-1]+1+x;
                                        a1[6]=a[6]+x;
                                        a1[13-j-1]=0;
                                        count1=13-j-1;
                                    }
                                    else
                                    if(j==13)
                                     {
                                         x=x+1;
                                         a1[j]=a[j]+1;
                                     }
                                     else
                                     {  
                                         if(j<13)
                                         a1[j]=a[j]+1;
                                         else
                                         {
                                           if(j!=20)
                                           a1[j-13-1]=a[j-13-1]+1;
                                           else
                                            a1[j-13-1]=a[j-13-1];
                                         }
                                     }
                              b[i]=kala(a1,depth+1,result-x);
                              nn=i;
                          }
                          else
                          {
                             
                              b[i]=1000;
                          }
                    }
                }
                if(count==0)
                {
              //      for(j=0;j<6;j++)
             //           sum=sum+a[j];
                    return (result);
                }
                else
                {
                    min=b[nn];
                    p=nn;
                    for(j=7;j<13;j++)
                      if(b[j]!=1000)
                      if(min>b[j])
                       {
                           min=b[j];
                           p=j;
                       }
                    return min;
                     
                }
            }
        }
        else
        {
            return result;
        }
    }

    
    
    
    /**
     * Returns a random ambo number (1-6) used when making
     * a random move.
     * 
     * @return Random ambo number
     */
    public int getRandom()
    {
        return 1 + (int)(Math.random() * 6);
    }
}
