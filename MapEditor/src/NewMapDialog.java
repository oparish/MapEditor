import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.text.NumberFormat;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;


@SuppressWarnings("serial")
public class NewMapDialog extends JDialog
{
	private int width;
	private int height;
	private int defaultWidth;
	private int defaultHeight;
	private ChangeField	widthField;
	private ChangeField	heightField;
	private ListenerButton okButton;
	
	NewMapDialog(JFrame owner, int currentWidth, int currentHeight)
	{
		super(owner);
		
		defaultWidth = currentWidth;
		defaultHeight = currentHeight;
		
	    setTitle("New Map");
	    setSize(500,100);
	    setModal(true);
		setLayout(new GridBagLayout());
		NumberFormat numberFormat = NumberFormat.getIntegerInstance();
		GridBagConstraints controlConstraints = setupControlConstraints();
		addWidthLabel(controlConstraints);
		addWidthField(numberFormat, controlConstraints);
		addHeightLabel(controlConstraints);
		addHeightField(numberFormat, controlConstraints);
		addOKButton(controlConstraints);
		addCancelButton(controlConstraints);
	}
	
	private GridBagConstraints setupControlConstraints()
	{
		GridBagConstraints controlConstraints = new GridBagConstraints();
		controlConstraints.gridy = 0;
		controlConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
		controlConstraints.insets = new Insets (2, 2, 2, 2);
		controlConstraints.weightx = 1;
		controlConstraints.weighty = 1;
		
		return controlConstraints;
	}
	
	private void addWidthLabel(GridBagConstraints controlConstraints)
	{
		JLabel widthLabel = new JLabel("Width:");
		controlConstraints.gridx = 0;
		add(widthLabel, controlConstraints);
	}
	
	private void addWidthField(NumberFormat numberFormat, GridBagConstraints controlConstraints)
	{
		widthField = new ChangeField(numberFormat)
		{
			public void actionPerformed(ActionEvent event)
			{
				int newWidth = Integer.parseInt(getText());
				if (newWidth>=0 && newWidth<256)
				{
					setValue(newWidth);
					checkValidity();
				}
				else
					setValue(defaultWidth);
			}
		
			public void focusLost(FocusEvent event)
			{
				if (isEditValid())
					actionPerformed(null);
				else
					setValue(defaultWidth);
			}
		};
		widthField.setValue(defaultWidth);
		controlConstraints.gridx = 1;
		add(widthField, controlConstraints);
	}
	
	private void addHeightLabel(GridBagConstraints controlConstraints)
	{
		JLabel widthLabel = new JLabel("Height:");
		controlConstraints.gridx = 2;
		add(widthLabel, controlConstraints);
	}
	
	private void addHeightField(NumberFormat numberFormat, GridBagConstraints controlConstraints)
	{
		heightField = new ChangeField(numberFormat)
		{
			public void actionPerformed(ActionEvent event)
			{
				int newHeight = Integer.parseInt(getText());
				if (newHeight>=0 && newHeight<256)
				{
					setValue(newHeight);
					checkValidity();
				}
				else
					setValue(defaultHeight);
			}
			
			public void focusLost(FocusEvent event)
			{
				if (isEditValid())
					actionPerformed(null);
				else
					setValue(defaultHeight);
			}
		};
		heightField.setValue(defaultHeight);
		controlConstraints.gridx = 3;
		add(heightField, controlConstraints);
	}
	
	private void addOKButton(GridBagConstraints controlConstraints)
	{
		okButton = new ListenerButton("OK")
		{
			public void actionPerformed(ActionEvent event)
			{
				setValues();
				close();
			}
		};
		controlConstraints.gridx=0;
		controlConstraints.gridy=1;
		controlConstraints.gridwidth=2;
		controlConstraints.anchor=GridBagConstraints.CENTER;
		add(okButton,controlConstraints);
	}
	
	private void addCancelButton(GridBagConstraints controlConstraints)
	{
		ListenerButton cancelButton = new ListenerButton("Cancel")
		{
			public void actionPerformed(ActionEvent event)
			{
				close();
			}
		};
		controlConstraints.gridx=2;
		controlConstraints.gridy=1;
		controlConstraints.gridwidth=2;
		controlConstraints.anchor=GridBagConstraints.CENTER;
		add(cancelButton,controlConstraints);
	}
	
	private void checkValidity()
	{
		if (widthField.getText()!=null && heightField.getText()!=null)
			okButton.setEnabled(true);
		else
			okButton.setEnabled(false);
	}
	
	private void setValues()
	{
		width = Integer.parseInt(widthField.getText());
		height = Integer.parseInt(heightField.getText());
	}
	
	private void close()
	{
		setVisible(false);
	}
	
	public int Width()
	{
		return width;
	}
	
	public int Height()
	{
		return height;
	}
}
