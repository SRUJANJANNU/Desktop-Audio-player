package sru.srujan.audio;

import java.awt.EventQueue;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import sru.srujan.audio.MoveMouseListener;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JTextPane;

public class Audio extends JFrame {

	private JPanel contentPane;
	DefaultListModel<String> listModel;
	JList<String> list;
	 JScrollPane scrollPane;
	 long pauseLoc, songLength;
		int playstatus=0,filepathresponse,trackNo=0;
		//play status 0 for stop , 1 for playing, 2 for paused
		public Player player;
		FileInputStream fis1;
		File[] selectedFile;
		BufferedInputStream bis1;
		JFileChooser fcPath=new JFileChooser();
		String strPath="",strPathNew;
		FileNameExtensionFilter filter ;
		JButton btnnext,btnplay,btnprev;
		MoveMouseListener mml1, mml2, mml3, mml4;
		protected int currentVolume;
		URL soundURL;
		//JTextPane txt;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Audio frame = new Audio();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	private FloatControl getVolumeControl() throws Exception {
	    try {
	        Mixer.Info mixers[] = AudioSystem.getMixerInfo();
	        for (Mixer.Info mixerInfo : mixers) {
	            Mixer mixer = AudioSystem.getMixer(mixerInfo);
	            mixer.open();

	            //we check only target type lines, because we are looking for "SPEAKER target port"
	            for (Line.Info info : mixer.getTargetLineInfo()) {
	                if (info.toString().contains("SPEAKER")) {
	                    Line line = mixer.getLine(info);
	                    try {
	                        line.open();
	                    } catch (IllegalArgumentException iae) {}
	                    return (FloatControl) line.getControl(FloatControl.Type.VOLUME);
	                }
	            }
	        }
	    } catch (Exception ex) {
	        System.out.println("problem creating volume control object:"+ex);
	        throw ex;
	    }
	    throw new Exception("unknown problem creating volume control object");
	}
	
	public Audio() {
		setTitle("AUDIO PLAYER");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 594, 387);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(176, 224, 230));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		

		JButton btnplay = new JButton("CHOOSE MP3");
		btnplay.setBackground(new Color(192, 192, 192));
		btnplay.setToolTipText("Select MP3 files");
		btnplay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				playPauseTrack();
				btnplay.setText("PLAY/PAUSE ▶️⏸️");
				//txt.setText(selectedFile[trackNo].getName());
			}
		});
		btnplay.setBounds(52, 315, 153, 21);
		contentPane.add(btnplay);
		
		JButton btnstop = new JButton("STOP ⏹️");
		btnstop.setBackground(new Color(192, 192, 192));
		btnstop.setToolTipText("Stop");
		btnstop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopPlayer();
				
				btnplay.setText("CHOOSE FILES");
			}
		});
		btnstop.setBounds(215, 315, 118, 21);
		contentPane.add(btnstop);
		
		
		JLabel img = new JLabel("New label");
		ImageIcon img2 = new ImageIcon(this.getClass().getResource("/img.jpg"));
		img.setIcon(img2);
		img.setBounds(17, 94, 343, 211);
		contentPane.add(img);
		
		listModel = new DefaultListModel<>();
		JList<String> list = new JList<>(listModel);
		list.setBackground(new Color(128, 128, 128));
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent listSelectionEvent) {
				if (!listSelectionEvent.getValueIsAdjusting()) 
	        	{//This line prevents double events
	        		jumpTrack(list.getSelectedIndex());
	        		list.setSelectionBackground(new Color(255, 128, 0));
	        		btnplay.setText("PLAY/PAUSE  ▶️⏸");
	        		//txt.setText("Files are chosen");
	            }
			}
		});
		list.setBounds(377, 100, 182, 198);
		contentPane.add(list);
		
		JLabel lblNewLabel = new JLabel("New label");
		ImageIcon img1 = new ImageIcon(this.getClass().getResource("/sru.png"));
		lblNewLabel.setIcon(img1);
		lblNewLabel.setBounds(52, 10, 444, 76);
		contentPane.add(lblNewLabel);
		
		JSlider slider = new JSlider();
		slider.setMajorTickSpacing(100);
		slider.setBackground(new Color(128, 128, 128));
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider src = (JSlider)e.getSource();
                if (src.getValue() % 5 !=0) return;
                float value = src.getValue() / 100.0f;
                try {
                    getVolumeControl().setValue(value);
                    //you can put a click play code here to have nice feedback when moving slider
                } catch (Exception ex) {
                    System.out.println(ex);
                } 
			}
		});
		slider.setBounds(377, 315, 182, 22);
		contentPane.add(slider);
		
		JLabel lblNewLabel_1 = new JLabel("🔈");
		lblNewLabel_1.setBounds(362, 317, 23, 17);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_1_1 = new JLabel(" 🔊");
		lblNewLabel_1_1.setBounds(557, 317, 23, 17);
		contentPane.add(lblNewLabel_1_1);
		
		
		
		
		contentPane.addKeyListener( new KeyListener() 
		{	
			@Override
			public void keyTyped( KeyEvent evt ){}
			
			@Override
			public void keyPressed( KeyEvent evt )
			{
				System.out.println("KEY "+evt.getKeyCode()+" pressed");
			}
			
			
			@Override
			public void keyReleased( KeyEvent evt ) 
			{
				System.out.println("KEY "+evt.getKeyCode()+" released");
			
			 if(evt.getKeyCode() == KeyEvent.VK_SPACE)
			{
				System.out.println("KEY "+evt.getKeyCode()+" released");
				playPauseTrack();
			}
			else if(evt.getKeyCode() == KeyEvent.VK_S)
			{
				stopPlayer();
			}
			else {}
			
			}//keyReleased() closed here
		
		} );
		//funList();
	}
	
	
	
	public void stopPlayer()
	{
		try
		{

			
			playstatus=0;
			strPath="";
			player.close();
			trackNo=0;
			listModel.removeAllElements();
			list.removeAll();
		}catch(Exception e) {}
	}//stopPlayer()_method closed here

	
	public void playSong(String path)
	{
		try 
		{
			fis1=new FileInputStream(path);
			bis1=new BufferedInputStream(fis1);
			player=new Player(bis1);
			songLength=fis1.available();			
			playstatus=1;			
			
			strPathNew=path+"";
			
		}
		catch (FileNotFoundException  | JavaLayerException ex) 
		{
     		playstatus=0;
//			
		} 
		catch (IOException e) 
		{}
		new Thread()
		{
			public void run()
			{
				try
				{
					player.play();
					
					if(player.isComplete())
					{

						try
						{
							if(trackNo==selectedFile.length-1)
								trackNo=-1;
							
							player.close();
							trackNo++;
						}
						catch(Exception e2){}
						
						if(trackNo == 0 && selectedFile.length-1 == 0)
						{
							jumpTrack(trackNo);
						}
						else
						{
							try
							{
								list.setSelectedIndex(trackNo);
							}catch(Exception e) {}
						}
					}
				}
				catch (JavaLayerException e) 
				{
					strPath="";
					playstatus=0;
				}
			}
		}.start();
		
	}//plays method closed here
	
	
	public void pausePlayer()
	{
		if(player != null)
		{
			try 
			{
				pauseLoc=fis1.available();
				player.close();
				playstatus=2;
				
			}
			catch (IOException e) 
			{}
		}
	}//pause method closed here
	
	
	public void resumePlayer()
	{
		
		try 
		{
			
			fis1=new FileInputStream(strPathNew);
			bis1=new BufferedInputStream(fis1);
			player=new Player(bis1);
			songLength=fis1.available();
			playstatus=1;
			fis1.skip(songLength-pauseLoc);
			
		}
		catch (FileNotFoundException  | JavaLayerException ex) 
		{
			playstatus=0;
		
			JOptionPane.showMessageDialog(null,ex);
			btnplay.setToolTipText("Select MP3 files");
			stopPlayer();
		} 
		catch (IOException e) 
		{}
		new Thread()
		{
			public void run()
			{
				try
				{
					player.play();
					if(player.isComplete())
					{
						btnnext.doClick();
						
					}
				}
				catch (JavaLayerException e) 
				{
					JOptionPane.showMessageDialog(null,e);
					strPath="";
					playstatus=0;
				}
			}
		}.start();
		
	}//resume method ended here
	
	
/*
	public void prevTrack()
	{
		try
		{   
		//	trackNo = 0;
			if(trackNo==0)
				trackNo=selectedFile.length;
		
			player.close();
			trackNo--;
		}
		catch(Exception e2) {}
		
		if(trackNo == selectedFile.length-1 && selectedFile.length-1 == 0)
		{
			jumpTrack(selectedFile.length-1);
		}
		else
		{
			try
			{
				list.setSelectedIndex(trackNo);
			}catch(Exception e) {}
		}
	}//prevTrack()_method closed here
	
	*/
	
	public void playPauseTrack()
	{
		if(playstatus==0)
		{	
			fcPath.setFileFilter(filter);
			fcPath.setMultiSelectionEnabled(true);
			fcPath.setCurrentDirectory(new File(System.getProperty("user.home")));
			filepathresponse = fcPath.showOpenDialog(contentPane);
			if (filepathresponse == JFileChooser.APPROVE_OPTION) 
			{
				// user selects a file
				selectedFile = null;
				selectedFile = fcPath.getSelectedFiles();
				strPath=selectedFile[0].getAbsolutePath();
				trackNo = 0;
				strPath=strPath.replace("\\", "\\\\");
				
				for(int i=0; i<selectedFile.length; i++)
				{
					listModel.addElement(selectedFile[i].getName());
				}
				
				playstatus = 1;
			  	trackNo=0;
				list.setSelectedIndex(trackNo);
				
			}	
		}
		
		else if(playstatus==1)
		{
		//	btnMPP.setIcon(iconPlay);
		//	lblAni.setIcon(iconStatic);
			playstatus=2;
			pausePlayer();
			
		}
		
		else
		{
			resumePlayer();
		}
	}//playPauseTrack()_method closed here
	
	/*
	public void nextTrack()
	{
		try
		{
			//trackNo = 0;
			if(trackNo==selectedFile.length-1)
				trackNo=-1;
			
			player.close();
			trackNo++;
		}
		catch(Exception e2){}
		
		if(trackNo == 0 && selectedFile.length-1 == 0)
		{
			jumpTrack(trackNo);
		}
		else
		{
			try
			{
				list.setSelectedIndex(trackNo);
			}catch(Exception e) {}
		}
	}//nextTrack()_method closed here
	
	
	*/
	
	public void jumpTrack(int index)
	{
		try
		{	
			player.close();
			trackNo = index;
			strPath=selectedFile[trackNo].getAbsolutePath();
			strPath=strPath.replace("\\", "\\\\");
		}
		catch(Exception e2){}
		if(filepathresponse==0 && playstatus!=0)
			playSong(strPath);
	}
}
