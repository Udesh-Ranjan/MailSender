package mailsender;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Dev Parzival
 * 20th March 2020
 * 8:30 am
 */
public class MailSender{
    
    static class Mail extends JPanel{
        JLabel name;
        JTextField _name;
        JLabel password;
        JPasswordField _password;
        JLabel recipient;
        JTextField _recipient;
        JLabel subject;
        JTextField _subject;
        
        public Mail(){
            //this.setBackground(Color.yellow);
            this.setLayout(new GridLayout(4,2,60,5));
            this.setSize(400,200);
            name=new JLabel("Sender Email");
            _name=new JTextField(10);
            password=new JLabel("Sender Password");
            _password=new JPasswordField(10);
            recipient=new JLabel("Receipent Email");
            _recipient=new JTextField(10);
            subject=new JLabel("Subject");
            _subject=new JTextField(10);
            this.add(name);
            this.add(_name);
            this.add(password);
            this.add(_password);
            this.add(recipient);
            this.add(_recipient);
            this.add(subject);
            this.add(_subject);
        }
    }
    static class Body extends JPanel{
        JLabel body;
        JTextArea _body;
        
        JButton attachment;
        JComboBox<Attachment>selectedAttachment;
        public Body(){
            //this.setBackground(Color.yellow);
            setLayout(new GridLayout(2,2,60,10));
            this.setSize(400,50);
            
            body=new JLabel("Body");
            _body=new JTextArea(10,30);
            
            attachment=new JButton("Add Attachment");
            selectedAttachment=new JComboBox<>();
            selectedAttachment.setSize(10,20);
            selectedAttachment.setEditable(true);
            attachment.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    
                    JFileChooser fileChooser=new JFileChooser();
                    fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                    int result=fileChooser.showOpenDialog(body);
                    if(result==JFileChooser.APPROVE_OPTION){
                        System.out.println("Approved");
                        try {
                            File file=fileChooser.getSelectedFile();
                            String path=file.getCanonicalPath();
                            Attachment attachment=new Attachment(file);
                            selectedAttachment.addItem(attachment);
//                            System.out.println(attachment);
//                            selectedAttachment.add("Hell",attachment);
//                            System.out.println(selectedAttachment);
                        } catch (IOException | MessagingException ex) {
                            Logger.getLogger(MailSender.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                    }
                }
            });
            
            JPanel panel1=new JPanel();
            JPanel panel2=new JPanel();
            
            panel1.add(attachment);
            panel2.add(selectedAttachment);
            
            add(body);
            add(_body);
            
            add(panel1);
            add(panel2);
        }
    }
    static class Attachment{
        String path;
        BodyPart bodypart;
        DataSource datasource;
        File file;
        public Attachment(File file) throws MessagingException, IOException {
            this.file=file;
            datasource=new FileDataSource(file.getAbsolutePath());
            bodypart=new MimeBodyPart();
            bodypart.setDataHandler(new DataHandler(datasource));
            bodypart.setFileName(file.getAbsolutePath());
            
        }
        public BodyPart getBodyPart(){
            return bodypart;
        }
        @Override
        public String toString(){
            
            String path=file.getAbsolutePath();
            
            for(int i=path.length()-1;i>=0;i--){
                if(path.charAt(i)=='/'||path.charAt(i)=='\\'){
                    return path.substring(i+1);
                }
            }
            
            return "";
        }
    }
    static class Butt extends JPanel{
        JButton reset;
        JButton send;
        
        Mail mail;
        Body body;
        
        Color sendBackground;
        
        public Butt(Mail mail,Body body) {
            this.mail=mail;
            this.body=body;
            
            //this.setBackground(Color.yellow);
            this.setLayout(new FlowLayout(FlowLayout.CENTER));
            this.setSize(700,200);
            
            reset=new JButton("Reset");
            send=new JButton("Send");
            
            sendBackground=send.getBackground();
            
            reset.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    mail._name.setText("");
                    mail._password.setText("");
                    mail._recipient.setText("");
                    mail._subject.setText("");
                    
                    body._body.setText("");
                    body.selectedAttachment.removeAllItems();
                    send.setBackground(sendBackground);
                }
            });
            
            send.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    
                    Properties properties=new Properties();
                    
                    properties.put("mail.smtp.auth", "true");
                    properties.put("mail.smtp.starttls.enable", "true");
                    properties.put("mail.smtp.host", "smtp.gmail.com");
                    properties.put("mail.smtp.port", "587");
                    
                    String senderMail=mail._name.getText().trim();
                    String senderMailPassword=mail._password.getText().trim();
                    String receiverMail=mail._recipient.getText().trim();
                    String subject=mail._subject.getText().trim();
                    String content=body._body.getText().trim();
                    
                    Session session=Session.getInstance(properties,new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication(){
                            return new PasswordAuthentication(senderMail,senderMailPassword);
                        }
                    });
                    Message message=new MimeMessage(session);
                    try{
                        message.setFrom(new InternetAddress(senderMail));
                        message.setRecipient(Message.RecipientType.TO,new InternetAddress(receiverMail));
                        message.setSubject(subject);
                        message.setText(content);
                        
                        Multipart multiPart=new MimeMultipart();
                        if(body.selectedAttachment.getItemCount()!=0){
                            for(int i=0;i<body.selectedAttachment.getItemCount();i++){
                                multiPart.addBodyPart(body.selectedAttachment.getItemAt(i).getBodyPart());
                            }
                            message.setContent(multiPart);
                        }
                        Transport.send(message);
                        send.setBackground(Color.green);
                    }
                    catch(MessagingException msgex){
                        msgex.printStackTrace();
                        send.setBackground(Color.red);
                    }
                }
            });
            add(reset);
            add(send);
        }
        
    }
    public MailSender(){
        JFrame frame=new JFrame();
        frame.setLayout(new GridLayout(3,1));
        frame.setSize(500,500);
        frame.setTitle("Mail Sender");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Mail mail=new Mail();
        Body body=new Body();
        Butt butt=new Butt(mail,body);
        
        frame.add(mail);
        frame.add(body);
        frame.add(butt);
        
        frame.setVisible(true);
    }
    public static void main(String[] args) {
        MailSender ms=new MailSender();
    }
    
}
