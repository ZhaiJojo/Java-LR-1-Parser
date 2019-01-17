package lr;
import java.io.*;
import javax.swing.*;
import java.util.*;
class thri{
    int beg;
    int nex;
    char ch;
};

class proj{
    int formula_numb;
    int part;
    char expc;
};

class action{
    char    ch;
    int     nxt_sta;
};

class pair{
	int state;
	char c;
	public pair(int state, char c){
		this.state=state;
		this.c=c;
	}
}

public class MainClass {
	static char G[][]=new char[20][20];    
	static int  length[]=new int[20];    
	static int  number = 0;
	static boolean tempofinput[]=new boolean[150];  
	static char str_vn[]=new char[20];      
	static int  size_vn = 0;
	static char str_vt[]=new char[150];    
	static int  size_vt = 0;
	static boolean first_vn[][]=new boolean [30][150];
	static char buffer[]=new char[50];           
	static int  bsize = 0;
	
	static thri trans[]=new thri[200];
	static int  size_trans = 0;

	static proj    items[][]=new proj [100][100];
	static int     Ccount = 0;
	static int     size_item[]=new int [100];

	static action    action_table[][]=new action [100][100];
	static int       size_act_table[]=new int[100];
	
	public static String getString(String file_name) {
    	String xmlString;
    	byte[] strBuffer = null;
    	int    flen = 0;
    	File xmlfile = new File(file_name);  
    	 try {
    		InputStream in = new FileInputStream(xmlfile);
    		flen = (int)xmlfile.length();
    		strBuffer = new byte[flen];
    		in.read(strBuffer, 0, flen);
    		in.close();
    	} catch (FileNotFoundException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	xmlString = new   String(strBuffer);     
    	
     
    	return xmlString;
    }
	
	static void Read_G()
	{
		String string_grammar=getString("grammar.txt");
		number=string_grammar.charAt(0)-'0';
	   
	    for(int i = 1; i <= number; i++){
	    	int k=1;
	        char temp;
	        int j = 0;
	        temp=string_grammar.charAt(k++);
	       
	        while(temp != '$'){
	            tempofinput[temp] = true;
	            G[i][j++] = temp;
	            temp=string_grammar.charAt(k++);
	           
	        }
	        length[i] = j;
	    }

	    G[0][0] = 'S';
	    G[0][1] = G[1][0];
	    length[0] = 2;

	    for(int i = 0; i < 64; i++)
	        if(tempofinput[i])
	           str_vt[size_vt++] = (char)i;
	    for(int i = 91; i < 128; i++)
	        if(tempofinput[i])
	           str_vt[size_vt++] = (char)i;
	    for(int i = 65; i < 91; i++)
	        if(tempofinput[i])
	           str_vn[size_vn++] = (char)i;
	}
	static void get_first(){
	    boolean flag1;
	    do{
	        flag1 = false;
	        for(int i = 1; i <= number; i++){
	           int t = 1;
	           boolean flag2;
	           do{
	               flag2 = false;
	               if (G[i][t] >= 'A' && G[i][t] <= 'Z'){
	                      for(int k = 0; k < 64; k++)
	                      if(first_vn[G[i][t]-'A'][k]==true&& !first_vn[G[i][0]-'A'][k]){
	                             first_vn[G[i][0]-'A'][k] = true;
	                             flag1 = true;
	                          }
	                      for(int k = 91; k < 128; k++)
	                      if(first_vn[G[i][t]-'A'][k]==true&& !first_vn[G[i][0]-'A'][k]){
	                             first_vn[G[i][0]-'A'][k] = true;
	                             flag1 = true;
	                          }
	                      if(first_vn[G[i][t]-'A'][64] == true){
	                          t++;
	                          flag2 = true;
	                      }
	                }
	                else if(!first_vn[G[i][0]-'A'][ G[i][t] ]){
	                      first_vn[G[i][0]-'A'][ G[i][t] ] = true;
	                      flag1 = true;
	                }
	           }while(flag2 && t < length[i]);
	           if(t == length[i])
	                first_vn[G[i][0]-'A'][26] = true;
	        }
	    }while(flag1);
	}
	
	static boolean is_in(proj temp,int T){
	    for(int i = 0; i < size_item[T]; i++)
	        if(temp.formula_numb == items[T][i].formula_numb && temp.part == items[T][i].part && temp.expc == items[T][i].expc)
	                return true;
	    return false;
	}
	
	static void  gete_expc(proj temp){
	    bsize = 0;
	    boolean flag;
	    int tt = temp.part;
	    do{
	        flag = false;
	        if(tt+1 >= length[temp.formula_numb]){
	                buffer[bsize++] = temp.expc;
	                return;
	        }
	        else if(G[temp.formula_numb][tt+1] < 'A' || G[temp.formula_numb][tt+1] > 'Z'){
	                buffer[bsize++] = G[temp.formula_numb][tt+1];
	                return;
	        }
	        else if(G[temp.formula_numb][tt+1] >= 'A' && G[temp.formula_numb][tt+1] <= 'Z'){
	                for(int i = 0; i < 64; i++){
	                   if(first_vn[ G[temp.formula_numb][tt+1]-'A' ][i])
	                      buffer[bsize++] = (char)i;
	                }
	                for(int i = 91; i < 128; i++){
	                   if(first_vn[ G[temp.formula_numb][tt+1]-'A' ][i])
	                      buffer[bsize++] = (char)i;
	                }
	                if(first_vn[ G[temp.formula_numb][tt+1]-'A' ][64]){
	                   tt++;
	                   flag = true;
	                }
	        }
	    }while(flag);
	}
	
	static void e_closure(int T){
	     for(int i = 0; i < size_item[T]; i++){
	       proj temp=new proj();
	       if(G[items[T][i].formula_numb][items[T][i].part] >= 'A' && G[items[T][i].formula_numb][items[T][i].part] <= 'Z'){
	           for(int j = 0; j < 20; j++)
	               if(G[j][0] == G[items[T][i].formula_numb][items[T][i].part]){
	                 gete_expc(items[T][i]);
	                 for(int k = 0; k < bsize; k++){
	                         temp.formula_numb = j;
	                         temp.part = 1;
	                         temp.expc = buffer[k];
	                         if(!is_in(temp,T))
	                           items[T][size_item[T]++] = temp;
	                 }
	                 bsize = 0;
	               }
	       }
	     }
	     return ;
	}
	
	static int is_contained()
	{
	    for(int i = 0; i < Ccount; i++){
	       int s = 0;      
	       if(size_item[i] == size_item[Ccount])
	                for(int j = 0; j < size_item[Ccount]; j++){
	                   for(int k = 0; k < size_item[i]; k++)
	                    if((items[Ccount][j].formula_numb==items[i][k].formula_numb)&&(items[Ccount][j].part == items[i][k].part) && (items[Ccount][j].expc == items[i][k].expc)){
	                                 s++;
	                                 break;
	                    }
	                }
	       if(s == size_item[Ccount])
	                return i;
	    }
	    return 0;
	}
	
	static void go(){
	    proj init=new proj();
	    init.expc = '#';
	    init.formula_numb = 0;
	    init.part = 1;
	    items[0][0] = init;
	    size_item[0]++;

	    e_closure(0);

	    for(int index = 0; index <= Ccount ; index++){
	        for(int j = 0; j < size_vt; j++){
	                proj    buf[]=new proj[50];
	                int     buf_size = 0;
	                proj    tp=new proj();
	                for(int p = 0; p < size_item[index]; p++){
	if((items[index][p].part<length[items[index][p].formula_numb])&&( G[ items[index][p].formula_numb ][ items[index][p].part ] == str_vt[j]) ){
	                           tp.formula_numb = items[index][p].formula_numb;
	                           tp.expc = items[index][p].expc;
	                           tp.part = items[index][p].part+1;
	                           buf[buf_size++] = tp;
	                   }
	                }
	                if(buf_size  != 0){
	                   Ccount++;
	                   for(int t = 0; t < buf_size; t++){
	                       items[Ccount][ size_item[Ccount]++ ] = buf[t];
	                   }
	                   e_closure(Ccount);
	                   int  next_state = is_contained();     
	                   if(next_state != 0){
	                       size_item[Ccount] = 0;
	                       Ccount--;
	                       trans[size_trans].beg = index;
	                       trans[size_trans].nex = next_state;
	                       trans[size_trans].ch = str_vt[j];
	                       size_trans++;
	                   }
	                   else{
	                       for(int i = 0; i < size_item[Ccount]; i++)

	                       trans[size_trans].beg = index;
	                       trans[size_trans].nex = Ccount;
	                       trans[size_trans].ch = str_vt[j];
	                       size_trans++;
	                   }
	                }
	        }              

	        for(int j = 0; j < size_vn; j++){
	                proj    buf[]=new proj[50];
	                int     buf_size = 0;
	                proj    tp=new proj();
	                for(int p = 0; p < size_item[index]; p++){
	if((items[index][p].part<length[items[index][p].formula_numb])&&( G[ items[index][p].formula_numb ][ items[index][p].part ] == str_vn[j]) ){
	                           tp.formula_numb = items[index][p].formula_numb;
	                           tp.expc = items[index][p].expc;
	                           tp.part = items[index][p].part+1;
	                           buf[buf_size++] = tp;
	                       }
	                }
	                if(buf_size  != 0){
	                   Ccount++;
	                   for(int t = 0; t < buf_size; t++){
	                       items[Ccount][ size_item[Ccount]++ ] = buf[t];
	                   }
	                   e_closure(Ccount);
	                   int  next_state = is_contained();    
	                   if(next_state != 0){
	                       size_item[Ccount] = 0;
	                       Ccount--;
	                       trans[size_trans].beg = index;
	                       trans[size_trans].nex = next_state;
	                       trans[size_trans].ch = str_vn[j];
	                       size_trans++;
	                   }
	                   else{
	                       
	                       for(int i = 0; i < size_item[Ccount]; i++)

	                       trans[size_trans].beg = index;
	                       trans[size_trans].nex = Ccount;
	                       trans[size_trans].ch = str_vn[j];
	                       size_trans++;
	                   }
	                }
	        }               
	    }
	}
	
	static void get_action(){
	    for(int i = 0; i < 100; i++)
	        size_act_table[i] = 0;

	    for(int i = 0; i <= Ccount; i++)        
	        for(int j = 0; j < size_item[i]; j++)
	                if(items[i][j].part == length[ items[i][j].formula_numb ] ){
	                    action_table[i][ size_act_table[i] ].ch = items[i][j].expc;
	                    action_table[i][ size_act_table[i]++ ].nxt_sta = items[i][j].formula_numb*(-1);
	                }
	    for(int i = 0; i < size_trans; i++){
	        int    t1 = trans[i].beg;
	        int    t2 = trans[i].nex;
	        char   tp = trans[i].ch;
	        action_table[t1][ size_act_table[t1] ].ch = tp;
	        action_table[t1][ size_act_table[t1]++ ].nxt_sta = t2;
	    }
	}

	public static void main(String[] args) {
		for(int i=0;i<200;i++) {
			trans[i]=new thri();
		}
		for(int i=0;i<100;i++) {
			for(int j=0;j<100;j++) {
				action_table[i][j]=new action();
			}
			
		}
		JFrame jf = new JFrame("LR(1)分析过程"); 
	    Object[][] tableData=new Object[4][];
	    Object[] columnTitle = {"状态栈","符号栈","输入串","动作"};
	    JTable table;

		   for(int i = 0; i< 150; i++)
		        tempofinput[i] = false;
		    for(int i= 0; i < 100; i++)
		        size_item[i] = 0;
		    for(int i = 0; i < 30; i++)
		        for(int j = 0; j < 150; j++)
		                first_vn[i][j] = false;



		    Read_G();        
		    get_first();     
		    go();
		    get_action();
		    for(int i = 0; i < Ccount; i++)
		        for(int j = 0; j < size_act_table[i]; j++){
		            char   tp = action_table[i][j].ch;
		            int    t  = action_table[i][j].nxt_sta;

		        }

		    bsize = 0;
		    String string_input=getString("input.txt");
		   for(;;bsize++) {
			   buffer[bsize]=string_input.charAt(bsize);
			   if(buffer[bsize] == '#') {
				   break;
			   }
		   }
		   bsize++;
		   Stack<pair> s=new Stack<pair>(); 
		    int    work_sta = 0;
		    int    index_buf = 0;
		    boolean   err;
		    boolean   done = false;
		    s.push(new pair(0,'#'));
		    int row=0;
		    String temp_state;
		    String temp_char;
		    do{
		    	work_sta = s.peek().state;
		        err =  true;

		        for(int i= 0; i < size_act_table[work_sta]; i++)
		        	
		            if(action_table[work_sta][i].ch == buffer[index_buf]){
		                    err = false;
		                    if(action_table[work_sta][i].nxt_sta == 0){
		                                    System.out.println("Accept!");
		                                    done = true;
		                                    break;
		                    }
		                    else if(action_table[work_sta][i].nxt_sta > 0){

		                    	s.push(new pair(action_table[work_sta][i].nxt_sta,action_table[work_sta][i].ch));
		                                    index_buf++;
		                                    break;
		                    }
		                    else{
		                       int tp = action_table[work_sta][i].nxt_sta*(-1);    
		                                    for(int k = 0; k < length[tp]-1; k++)
		                                          s.pop();
		                                    --index_buf;
		                                    buffer[index_buf] = G[tp][0];
		                                    break;
		                    }
		            }
		        row++;
		    }while(done == false && err == false);
		    if(!done)
                System.out.println("!");
//	        table = new JTable(tableData , columnTitle);      
//	        jf.add(new JScrollPane(table));  
//	        jf.pack();  
//	        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
//	        jf.setVisible(true);     

		}
	

}
