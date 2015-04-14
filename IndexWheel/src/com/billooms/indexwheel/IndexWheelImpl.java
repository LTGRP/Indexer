package com.billooms.indexwheel;

import com.billooms.indexwheel.drawables.Circle;
import com.billooms.indexwheel.drawables.Dot;
import com.billooms.indexwheel.drawables.Text;
import com.billooms.indexwheel.api.IndexWheel;
import com.billooms.indexwheel.drawables.Pt;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import org.openide.util.lookup.ServiceProvider;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Implementation of an index wheel on a lathe.
 * @author Bill Ooms. Copyright 2011 Studio of Bill Ooms. All rights reserved.
 */
@ServiceProvider(service = IndexWheel.class)
public class IndexWheelImpl implements IndexWheel {

	private final static String ID_PREFIX = "idx";
	private final static int DEFAULT_HOLES = 48;
    private final static double HOLES_RADIUS = WHEEL_RADIUS - 0.1;
    private final static double TEXT_RADIUS = WHEEL_RADIUS - 0.3;
    private final static Font ARROW_FONT = new Font("SansSerif", Font.BOLD, 30);	// Font that will be used for arrow.
    private final static Color ARROW_COLOR = Color.BLUE;
    private final static String ARROW_UNI = "\u27A1";		// unicode for rightarrow symbol
//	private final static String ARROW_UNI = "\u2B05";		// unicode for leftarrow symbol
    private final static Font NAME_FONT = new Font("SansSerif", Font.BOLD, 30);	// Font that will be used for name.
    private final static Color NAME_COLOR = Color.BLACK;
    private final static String DEFAULT_NAME = "Index" + DEFAULT_HOLES;
	private final static Font NUMBER_FONT = new Font("SansSerif", Font.PLAIN, 12);
	private final static Font HIGHLIGHT_FONT = new Font("SansSerif", Font.BOLD, 16);	// highlight for one index hole
	private final static Color HIGHLIGHT_COLOR = Color.BLUE;

    private final static DecimalFormat F4 = new DecimalFormat("0.0000");
    
    // Objects for drawing
    private Circle outline;			// the outline of the wheel
    private Dot[] holes;			// array of holes
    private Text[] numbers;			// text for hole number
    private Text name;				// name for the wheel
    private Text arrow;				// points to current location of the wheel
	
	private static int countID = 0;		// counter for generating a unique ID
	private final String id;			// unique ID which doesn't change
    private int numHoles = DEFAULT_HOLES;	// total number of holes in the wheel
    private double phase = 0.0;		// fractional phase in the range 0.0 to 1.0
    private static double rotation = 0.0;	// current rotation in degrees shared by all wheels
//	private int resolution = 2600;		// micro-steps per spindle rotation -- use for testing
	private int resolution = 0;			// micro-steps per spindle rotation
	
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	/**
	 * Construct the IndexWheel.
	 */
	public IndexWheelImpl() {
		id = ID_PREFIX + countID;		// unique ID which does not change
		
		// arrow on left side
		arrow = new Text(new Point2D.Double(-WHEEL_RADIUS, 0.0), ARROW_UNI, ARROW_COLOR);
		arrow.setFont(ARROW_FONT);
		arrow.setJustify(Text.Justify.RIGHT);

		name = new Text(new Point2D.Double(0.0, 0.0), DEFAULT_NAME, NAME_COLOR);
		name.setFont(NAME_FONT);
		name.setJustify(Text.Justify.CENTER);

		outline = new Circle(new Point2D.Double(0.0, 0.0), WHEEL_RADIUS, Color.BLACK);
		
		makeWheel();
		countID++;
	}
	
	/**
	 * Construct an IndexWheel from the given xml document element.
	 * @param element xml document element
	 */
	public IndexWheelImpl(Element element) {
		this();					// initialize all the basic stuff
		
		numHoles = Integer.parseInt(element.getAttribute("nHoles"));
		phase = Double.parseDouble(element.getAttribute("phase"));
		name.setText(element.getAttribute("name"));
		makeWheel();			// must do again in case the number of holes has changed
		
		NodeList list = element.getElementsByTagName("fill");	// find all <hole>
		for (int i = 0; i < list.getLength(); i++) {
			fillHole(Integer.parseInt(((Element)list.item(i)).getAttribute("hole")), true);
		}
	}

	/**
	 * Make all the features of the wheel.
	 * (the first time, or when the number of holes changes).
	 * All holes are un-filled.
	 */
	private void makeWheel() {
		holes = new Dot[numHoles];
		numbers = new Text[numHoles];

		double delta = 2.0*Math.PI/numHoles;	// radians per hole
		double ph = phase * delta;				// additional phase shift (radians)
		// arrow on left side
		arrow.moveTo(new Point2D.Double((-WHEEL_RADIUS)*Math.cos(-ph), (WHEEL_RADIUS)*Math.sin(-ph)));

		double angle;		// in radians
		for (int i = 0; i < holes.length; i++) {
			angle = (double)i * delta - Math.toRadians(rotation);
			// holes on left side
			holes[i] = new Dot(new Point2D.Double(-HOLES_RADIUS*Math.cos(angle), HOLES_RADIUS*Math.sin(angle)));
			holes[i].setFill(false);
			numbers[i] = new Text(new Point2D.Double(-TEXT_RADIUS*Math.cos(angle), TEXT_RADIUS*Math.sin(angle)), Integer.toString(i));
			numbers[i].setFont(NUMBER_FONT);
		}
	}

	/**
	 * Update the location of everything on the wheel.
	 */
	protected void updateWheel() {
		double delta = 2.0*Math.PI/numHoles;
		double ph = phase * delta;
		// arrow on left side
		arrow.moveTo(new Point2D.Double((-WHEEL_RADIUS)*Math.cos(-ph), (WHEEL_RADIUS)*Math.sin(-ph)));

		double angle;		// in radians
		for (int i = 0; i < holes.length; i++) {
			angle = (double)i * delta - Math.toRadians(rotation);
			// holes on left side
			holes[i].moveTo(new Point2D.Double(-HOLES_RADIUS*Math.cos(angle), HOLES_RADIUS*Math.sin(angle)));
			numbers[i].moveTo(new Point2D.Double(-TEXT_RADIUS*Math.cos(angle), TEXT_RADIUS*Math.sin(angle)));
			numbers[i].setFont(NUMBER_FONT);		// make sure all fonts are reset
			numbers[i].setColor(Pt.DEFAULT_COLOR);
		}
		double rot = rotation;
		while (rot >= 360.0) {		// make sure rot is less than 360		
			rot = rot - 360.0;
		}
		if (resolution != 0) {		// bold font for the hole
			for (int i = 0; i < holes.length; i++) {
				if ((degreeToStep(angleOf(i) + phaseDeg(), resolution) == degreeToStep(rot, resolution)) && 
					(isFilled(i))) {
					numbers[i].setFont(HIGHLIGHT_FONT);
					numbers[i].setColor(HIGHLIGHT_COLOR);
				}
			}
		}
	}

    /**
     * Paint the object.
     * @param g2d Graphics2D
     */
	@Override
    public void paint(Graphics2D g2d) {
		outline.paint(g2d);			// draw the outline
		for (Dot h : holes) {		// draw all the holes
			h.paint(g2d);
		}
		for (Text t : numbers) {		// draw the text numbers
			t.paint(g2d);
		}
		arrow.paint(g2d);			// draw the arrow
		name.paint(g2d);			// draw the name
    }
	
	/**
	 * Get this object's unique ID which does not change.
	 * @return unique id
	 */
	@Override
	public String getID() {
		return id;
	}

	/**
	 * Get the string name.
	 * @return name
	 */
	@Override
	public String getName() {
		return name.getText();
	}

	/**
	 * Set the string name. 
	 * This fires a PROP_NAME property change with the old and new names. 
	 * @param str name
	 */
	@Override
	public void setName(String str) {
		String old = name.getText();
		name.setText(str);
		pcs.firePropertyChange(PROP_NAME, old, str);
	}

	/**
	 * Get the number of holes in the wheel.
	 * @return total number of holes.
	 */
	@Override
	public int getNumHoles() {
		return numHoles;
	}

	/**
	 * Set the number of holes in the wheel. 
	 * Values less than 1 will be interpreted as 1. 
	 * This fires a PROP_NUMHOLES property change with the old and new values. 
	 * Note: this makes all holes un-filled.
	 * @param n number of holes
	 */
	@Override
	public void setNumHoles(int n) {
		int old = numHoles;
		this.numHoles = Math.max(n, 1);	// don't go less than 1
		makeWheel();
		pcs.firePropertyChange(PROP_NUMHOLES, old, numHoles);
	}

	/**
	 * Get the phase shift of the wheel.
	 * @return phase in fraction of the hole spacing (range is 0.0 to 1.0)
	 */
	@Override
	public double getPhase() {
		return phase;
	}

	/**
	 * Set the phase of the wheel. 
	 * This fires a PROP_PHASE property change with the old and new values. 
	 * @param ph phase in fraction of the hole spacing (range is 0.0 to 1.0)
	 */
	@Override
	public void setPhase(double ph) {
		double old = phase;
		this.phase = ph;
		if (ph < 0.0) {			// must be in range 0.0 to 1.0
			this.phase = 0.0;
		}
		if (ph > 1.0) {
			this.phase = 1.0;
		}
		updateWheel();
		pcs.firePropertyChange(PROP_PHASE, old, phase);
	}

	/**
	 * Get the current rotation of the wheels in degrees. 
	 * Positive rotation is counter-clockwise.
	 * Note that all wheels share a common rotation. 
	 * @return current rotation in degrees
	 */
	@Override
	public double getRotation() {
		return rotation;
	}

	/**
	 * Set the current rotation of the wheels to the given value. 
	 * Positive rotation is counter-clockwise.
	 * Note that all wheels share a common rotation as if they were all 
	 * connected together on a single shaft.
	 * This fires a PROP_ROTATION property change with the old and new values. 
	 * @param r new rotation in degrees
	 */
	@Override
	public void setRotation(double r) {
		double old = rotation;
		IndexWheelImpl.rotation = r;
		updateWheel();
		pcs.firePropertyChange(PROP_ROTATION, old, rotation);
	}

	/**
	 * Get the rotation of the next filled hole. 
	 * The rotation might wrap around from the largest number back to zero, 
	 * so the number might be more than 360.0 degrees.
	 * Positive rotation is counter-clockwise.
	 * Note that this does not change the rotation of the wheel. 
	 * It only gets the rotation of the next filled hole. 
	 * Use setRotation(r) to change the rotation. 
	 * @param res The resolution of the spindle in micro-steps per revolution.
	 * @return the rotation of the next filled hole in degrees. 
	 * This is a positive number representing the absolute rotation (not incremental). 
	 * Returns the current rotation if there are no filled holes.
	 */
	@Override
	public double getRotationOfNext(int res) {
		this.resolution = res;		// save it for other use (comment this out for testing)
		if (getNumFilled() <= 0) {	// do nothing if no holes are filled
			return rotation;
		}

		int idx = 0;		// find the next filled hole
		// Must make allowances for round-off due to resolution, so 
		// convert the angles to steps prior to comparison.
		while (!isFilled(idx) || 
			   (degreeToStep(angleOf(idx) + phaseDeg(), res) <= degreeToStep(rotation, res))) {
			idx++;
		}
		return angleOf(idx) + phaseDeg();
	}

	/**
	 * Determine if hole n is filled.
	 * Note that n can be negative or n > numHoles. 
	 * If n > numHoles, then it wraps around.
	 * @param n hole number
	 * @return true=filled; false=not filled
	 */
	private boolean isFilled(int n) {
		int nn = n;
		while (nn < 0) {		// don't want a negative from modulus
			nn += numHoles;
		}
		nn = nn % numHoles;
		return holes[nn].getFill();
	}

	/**
	 * Get the angle in degrees of the given hole.
	 * @param n hole number (can be negative or > numHoles)
	 * @return angle in degrees
	 */
	private double angleOf(int n) {
		return n * 360.0/numHoles;
	}

	/**
	 * The phase shift in absolute degrees.
	 * @return phase in absolute degrees
	 */
	private double phaseDeg() {
		return phase*360.0/numHoles;
	}

	/**
     * Convert degrees to steps. 
	 * This should be the same as is used in RotationStage.java
     * @param deg degrees
	 * @param res resolution in micro-steps per rotation
     * @return steps
	 */
	private long degreeToStep(double deg, int res) {
		return Math.round(deg*res/360.0);
	}

	/**
	 * Clear all the holes (i.e. not filled). 
	 * This fires a PROP_CLEARALL property change. 
	 */
	@Override
	public void clearAll() {
		for (Dot h : holes) {
			h.setFill(false);
		}
		pcs.firePropertyChange(PROP_CLEARALL, null, null);
	}

	/**
	 * Fill all the holes.
	 * This fires a PROP_FILLALL property change. 
	 */
	@Override
	public void fillAll() {
		for (Dot h : holes) {
			h.setFill(true);
		}
		pcs.firePropertyChange(PROP_FILLALL, null, null);
	}

	/**
	 * Fill or clear a specific hole. 
	 * This fires a PROP_FILLHOLE property change with the number of the hole filled. 
	 * Note: negative numbers or values >= numHoles will be ignored
	 * @param n hole to fill
	 * @param f true=fill, false=clear
	 */
	@Override
	public final void fillHole(int n, boolean f) {
		if ((n >= holes.length) || (n < 0)) {
			return;
		}
		holes[n].setFill(true);
		pcs.firePropertyChange(PROP_FILLHOLE, null, n);
	}
	
	/**
	 * Get the number of holes in the wheel that are filled.
	 * @return number of filled holes
	 */
	private int getNumFilled() {
		int n = 0;
		for (Dot dot : holes) {
			if (dot.getFill()) {
				n++;
			}
		}
		return n;
	}
	
	/**
	 * Toggle the fill of the hole nearest the given point (within given distance).
	 * This fires a PROP_TOGGLEFILL property change with the number of the hole. 
	 * @param p Point in pixels (screen coordinates)
	 * @param dis distance to measure
	 * @return true=hole was found and changed, false=nothing changed
	 */
	@Override
	public boolean toggleHoleNearest(Point2D.Double p, double dis) {
        Dot d = null;
        double minSep = dis;
        for (Dot h : holes) {
            double sep = h.separation(p);
            if (sep < minSep) {
                minSep = sep;
                d = h;
            }
        }
		if (d == null) {
			return false;
		}
		d.setFill(!d.getFill());		// toggle fill
		pcs.firePropertyChange(PROP_TOGGLEFILL, null, d);
		return true;
	}

	/**
	 * Write IndexWheel information to an xml file.
	 * @param out output stream for writing
	 */
	@Override
	public void writeXML(PrintWriter out) {
		out.println("  <IndexWheel nHoles='" + holes.length + 
				"' phase='" + F4.format(phase) +
				"' name='" + name.getText() + 
				"'>");
		for (int i = 0; i < holes.length; i++) {
			if (holes[i].getFill()) {		// only write out the filled holes
				out.println("    <fill hole='" + i + "'/>");
			}
		}
		out.println("  </IndexWheel>");
	}

	/**
	 * Add the given PropertyChangeListener to this object
	 * @param listener
	 */
	@Override
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

	/**
	 * Remove the given PropertyChangeListener from this object
	 * @param listener
	 */
	@Override
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }
}
