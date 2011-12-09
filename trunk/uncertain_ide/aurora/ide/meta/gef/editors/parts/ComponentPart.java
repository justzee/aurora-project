package aurora.ide.meta.gef.editors.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.graph.CompoundDirectedGraph;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.draw2d.graph.Subgraph;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.IProperties;

public abstract class ComponentPart extends AbstractGraphicalEditPart implements
		PropertyChangeListener, IProperties {
	static final Insets PADDING = new Insets(8, 6, 8, 6);
	static final Insets INNER_PADDING = new Insets(0);
	static int row = 0;


	public void propertyChange(PropertyChangeEvent evt) {
		this.getFigure().getBounds();
		String prop = evt.getPropertyName();
		if (!IProperties.CHILDREN.equals(prop))
			this.refreshVisuals();
	}

	@Override
	public void activate() {
		super.activate();
		getComponent().addPropertyChangeListener(this);
	}

	public AuroraComponent getComponent() {
		return (AuroraComponent) getModel();
	}

	@Override
	public void deactivate() {
		getComponent().removePropertyChangeListener(this);
		super.deactivate();
	}

	public void contributeNodesToGraph(CompoundDirectedGraph graph, Subgraph s,
			Map map) {
		GraphAnimation.recordInitialState(getContentPane());
//		Subgraph me = new Subgraph(this, s);
//		// me.rowOrder = getActivity().getSortIndex();
//		me.outgoingOffset = 5;
//		me.incomingOffset = 5;
//		IFigure fig = getFigure();
//		if (fig instanceof Figure) {
//			me.width = fig.getPreferredSize(me.width, me.height).width;
//			// int tagHeight = ((SubgraphFigure) fig).getHeader()
//			// .getPreferredSize().height;
//			me.height = 80;
//			
//			me.width = 160;
//			int tagHeight = 50;
//			me.insets.top = tagHeight;
//			me.insets.left = 0;
//			me.insets.bottom = tagHeight;
//		}
//		me.innerPadding = INNER_PADDING;
//		me.setPadding(PADDING);
//		map.put(this, me);
//		graph.nodes.add(me);
		
		row++;
		
//		Node n = new Node(this, s);
////		n.outgoingOffset = getAnchorOffset();
////		n.incomingOffset = getAnchorOffset();
//		n.width = getFigure().getPreferredSize().width;
//		n.height = getFigure().getPreferredSize().height;
//		n.setPadding(new Insets(10, 8, 10, 12));
//		map.put(this, n);
//		graph.nodes.add(n);
		
		Subgraph me = new Subgraph(this, s);
//		me.setRowConstraint(row);
		me.insets.bottom =80;
		me.insets.top = 80;
		me.insets.left =10;
		me.innerPadding = INNER_PADDING;
		me.setPadding(PADDING);
        me.width = this.getFigure().getPreferredSize().width;
        me.height = this.getFigure().getPreferredSize().height;
//        me.setPadding(new Insets(10,8,10,12));
        map.put(this, me);
        graph.nodes.add(me);
//	
//		
//		for (int i = 0; i < getChildren().size(); i++) {
//			ComponentPart activity = (ComponentPart) getChildren().get(i);
//			activity.contributeNodesToGraph(graph, me, map);
//		}
        this.getFigure();
	}

	public void applyGraphResults(CompoundDirectedGraph graph, Map partsToNodes) {

		Node n = (Node) partsToNodes.get(this);
		
		getFigure().setBounds(new Rectangle(n.x, n.y, n.width, n.height));

	}

}
