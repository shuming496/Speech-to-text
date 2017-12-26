package org.xianlv.stt;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.xianlv.stt.response.TextResult;

public class STTUI extends JFrame {

	private JPanel panel;
	// private JLabel tip_label;
	private JTextArea info_message;
	private JButton button_input, button_output, button_start;
	private JLabel input_path_tip, output_path_tip, output_type_tip;
	private JTextField input_path, output_path;
	private JCheckBox plain_text;
	private JScrollPane scroll;
	private JMenuBar menu_bar;
	private JMenu option, about;
	private JMenuItem account, default_path, swap_path, bug_report, about_content_2, about_content_3, about_content_4,
			about_content_5;
	private String default_inpath_value, default_outpath_value, default_swappath_value;
	private boolean output_type_plain, output_type_json;
	private List<File> files = new ArrayList<File>();

	public STTUI() {
	}

	public STTUI(String frame_name) {
		super(frame_name);
		setBounds(200, 200, 500, 400);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//
		panel = new JPanel();
		add(panel);

		menu_bar = new JMenuBar();
		option = new JMenu("选项(O)");
		about = new JMenu("关于(A)");

		// account = new JMenuItem("账户设置");
		default_path = new JMenuItem("默认输出路径");
		// swap_path = new JMenuItem("默认临时转码路径");
		// bug_report = new JMenuItem("问题反馈");
		about_content_2 = new JMenuItem("开发者：熹微");
		about_content_3 = new JMenuItem("运行平台：Java 1.8");
		about_content_4 = new JMenuItem("语音转录：阿里云语音转录引擎 & 阿里云OSS存储");
		about_content_5 = new JMenuItem("版本: v1.1");

		// option.add(account);
		option.add(default_path);
		// option.add(swap_path);
		// option.addSeparator();
		// option.add(bug_report);
		about.addSeparator();
		about.add(about_content_2);
		about.addSeparator();
		about.add(about_content_3);
		about.addSeparator();
		about.add(about_content_4);
		about.addSeparator();
		about.add(about_content_5);

		option.setFont(new Font("Serif", Font.PLAIN, 15));
		about.setFont(new Font("Serif", Font.PLAIN, 15));

		menu_bar.add(option);
		menu_bar.add(about);

		// account.addActionListener(new AccountHandler());
		default_path.addActionListener(new Default_pathHandler());
		// swap_path.addActionListener(new Swap_pathHandler());
		// bug_report.addActionListener(new Bug_reportHandler());
		setJMenuBar(menu_bar);

		//
		button_input = new JButton("请选择音频文件路径");
		button_output = new JButton("请选择输出文本路径");
		button_start = new JButton("开始转换");

		button_input.setFont(new Font("Serif", Font.PLAIN, 15));
		button_output.setFont(new Font("Serif", Font.PLAIN, 15));
		button_start.setFont(new Font("Serif", Font.PLAIN, 15));

		panel.add(button_input);
		panel.add(button_start);
		panel.add(button_output);

		button_input.addActionListener(new Button_inputHandler());
		button_output.addActionListener(new Button_outputHandler());
		button_start.addActionListener(new Button_startHandler());

		input_path_tip = new JLabel("输入路径：");
		output_path_tip = new JLabel("输出路径：");
		input_path_tip.setFont(new Font("Serif", Font.PLAIN, 12));
		output_path_tip.setFont(new Font("Serif", Font.PLAIN, 12));

		input_path = new JTextField(45);
		output_path = new JTextField(45);
		input_path.setFont(new Font("Serif", Font.PLAIN, 12));
		output_path.setFont(new Font("Serif", Font.PLAIN, 12));
		try {
			Scanner configinfo = new Scanner(new File("configinfo"));
			default_outpath_value = configinfo.nextLine().replace("default_outpath:", "");
			default_swappath_value = configinfo.nextLine().replace("default_swappath:", "");
			output_path.setText(default_outpath_value);

			Scanner config = new Scanner(new File("config.properties"));
			String s = new String();
			for (int i = 0; i < 9; i++) {
				s += config.nextLine() + "\n";
			}
			s += "store_path=" + default_swappath_value.replace("\\", "\\\\");

			PrintWriter outfile = new PrintWriter("config.properties");
			outfile.print(s);
			outfile.close();
		} catch (FileNotFoundException e) {
			System.out.println("No Files");
		}

		panel.add(input_path_tip);
		panel.add(input_path);
		panel.add(output_path_tip);
		panel.add(output_path);

		output_type_tip = new JLabel("消息：");
		panel.add(output_type_tip);

		info_message = new JTextArea("准备就绪... \n", 10, 39);
		info_message.setLineWrap(true);
		info_message.setSize(new Dimension(600, 400));
		info_message.setFont(new Font("Serif", Font.PLAIN, 12));
		info_message.setMargin(new Insets(5, 5, 10, 10));
		info_message.setWrapStyleWord(true);
		info_message.getDocument().addDocumentListener(new DocumentListener() {

			public void changedUpdate(DocumentEvent e) {
			}

			public void insertUpdate(DocumentEvent e) {
				info_message.setCaretPosition(info_message.getText().length());
			}

			public void removeUpdate(DocumentEvent e) {
			}
		});

		scroll = new JScrollPane(info_message);
		scroll.setSize(new Dimension(340, 395));
		scroll.setBounds(5, 45, 650, 400);

		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		// scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());

		panel.add(scroll);

		setVisible(true);
	}

	private class PlainHandler implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			if (plain_text.isSelected()) {
				output_type_plain = true;
			} else {
				output_type_plain = false;
			}

		}
	}

//	private class Bug_reportHandler implements ActionListener {
//		public void actionPerformed(ActionEvent ae) {
//			JOptionPane.showMessageDialog(STTUI.this,
//					"1. ");
//		}
//	}

	private class Button_inputHandler implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			JFileChooser infile = new JFileChooser();
			infile.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			infile.showDialog(new JLabel(), "选择输入文件");
			File file = infile.getSelectedFile();
			input_path.setText(file.getAbsolutePath());
			List<File> files = traverseFolder(file);
		}
	}

	public List<File> traverseFolder(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] listFiles = file.listFiles();
				if (listFiles.length == 0) {
					info_message.append("文件夹是空的~! \n");
					return null;
				} else {
					for (File fi : listFiles) {
						traverseFolder(fi);
					}
				}
			} else {
				files.add(file);
			}

		} else {
			info_message.append("文件或文件夹不存在~! \n");
			return null;
		}
		return files;
	}

	private class Button_outputHandler implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			JFileChooser infile = new JFileChooser();
			infile.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			infile.showDialog(new JLabel(), "选择输出文件夹");
			File file = infile.getSelectedFile();
			output_path.setText(file.getAbsolutePath());
			default_outpath_value = file.getAbsolutePath();
		}
	}

	private class AccountHandler implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			String app_id = JOptionPane.showInputDialog("请输入APP ID:");
			String secret_key = JOptionPane.showInputDialog("请输入secret key:");
			try {
				Scanner infile = new Scanner(new File("config.properties"));
				String s = new String();
				for (int i = 0; i < 10; i++) {
					if (i != 1 && i != 3)
						s += infile.nextLine() + "\n";
					else if (i == 1) {
						s += "app_id=" + app_id + "\n";
						infile.nextLine();
					} else {
						s += "secret_key=" + secret_key + "\n";
						infile.nextLine();
					}
				}
				infile.close();

				PrintWriter outfile = new PrintWriter("config.properties");
				outfile.print(s);
				outfile.close();
			} catch (FileNotFoundException e) {
			}
		}
	}

	private class Default_pathHandler implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			JFileChooser outpath = new JFileChooser();
			outpath.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			outpath.showDialog(new JLabel(), "选择默认输出文件夹");
			File file = outpath.getSelectedFile();
			default_outpath_value = file.getAbsolutePath();
			output_path.setText(default_outpath_value);

			try {
				Scanner configinfo = new Scanner(new File("configinfo"));
				String s = new String();
				configinfo.nextLine();
				s += "default_outpath:" + default_outpath_value + "\n";
				s += configinfo.nextLine() + "\n";
				configinfo.close();

				PrintWriter outfile = new PrintWriter("configinfo");
				outfile.print(s);
				outfile.close();
			} catch (FileNotFoundException e) {
			}
		}
	}

	private class Swap_pathHandler implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			JFileChooser swappath = new JFileChooser();
			swappath.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			swappath.showDialog(new JLabel(), "选择中转文件夹");
			File file = swappath.getSelectedFile();
			default_swappath_value = file.getAbsolutePath();

			try {
				Scanner configinfo = new Scanner(new File("configinfo"));
				String s = new String();
				s += configinfo.nextLine() + "\n";
				configinfo.nextLine();
				s += "default_swappath:" + default_swappath_value + "\n";
				configinfo.close();

				PrintWriter outfile = new PrintWriter("configinfo");
				outfile.print(s);
				outfile.close();
			} catch (FileNotFoundException e) {
			}
		}
	}

	private class Button_startHandler implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			info_message.setText("");
			info_message.append("任务已经开始... \n");
			info_message.append("当前任务数量: " + files.size() + "个  \n");
			info_message.setCaretPosition(info_message.getDocument().getLength());

			for (int i = 0; i < files.size(); i++) {

				default_inpath_value = files.get(i).getAbsolutePath();
				STT s2t = new STT(default_inpath_value, info_message);

				TextResult result = s2t.start();
				String input_file = default_inpath_value.replace("\\", "\\\\");
				String fileName = input_file.substring(input_file.lastIndexOf("\\") + 1);

				String output_file_name = default_outpath_value.replace("\\", "\\\\") + "\\"
						+ input_file.split("\\\\")[input_file.split("\\\\").length - 1] + "_text.txt";

				if ("ERROR".equals(result.getStatus())) {
					JOptionPane.showMessageDialog(STTUI.this, "任务失败，失败信息：" + result.getTexts().get(0));
				} else {
					try {

						BufferedWriter fileWriter = new BufferedWriter(
								new OutputStreamWriter(new FileOutputStream(output_file_name, true), "UTF-8"));
						// FileWriter fileWriter = new FileWriter(output_file_name, true);
						java.util.List<String> texts = result.getTexts();
						for (String string : texts) {
							fileWriter.append(string);
							fileWriter.append(System.getProperty("line.separator"));
							fileWriter.flush();
						}
						fileWriter.close();

						info_message.append("[" + fileName + "] 转写完毕... \n");
						if (i + 1 == files.size()) {
							info_message.setText("");
							info_message.append("全部任务已经完成！");
							info_message.setCaretPosition(info_message.getDocument().getLength());
						} else {
							info_message.setText("");
							info_message.append("剩余任务数量: " + (files.size() - i - 1) + "个  \n");
							info_message.setCaretPosition(info_message.getDocument().getLength());
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
