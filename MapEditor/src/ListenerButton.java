import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

@SuppressWarnings("serial")
public class ListenerButton extends JButton implements ActionListener
{
	ListenerButton(String contentText)
	{
		Dimension size = new Dimension(160,30);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
		setText(contentText);
		addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent event)
	{

	}
}
