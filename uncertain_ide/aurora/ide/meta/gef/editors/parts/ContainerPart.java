package aurora.ide.meta.gef.editors.parts;

import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.graph.CompoundDirectedGraph;
import org.eclipse.draw2d.graph.Subgraph;

import aurora.ide.meta.gef.editors.figures.BoxFigure;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.IProperties;

public abstract class ContainerPart extends ComponentPart {

	protected List<?> getModelChildren() {
		return getContainer().getChildren();
	}

	private Container getContainer() {
		return (Container) getModel();
	}
	

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		String prop = evt.getPropertyName();
		if (IProperties.CHILDREN.equals(prop))
			refreshChildren();
	}
	public void contributeNodesToGraph(CompoundDirectedGraph graph, Subgraph s,
			Map map) {
		if(!(this instanceof ViewDiagramPart)){
			super.contributeNodesToGraph(graph, s, map);
			return;
		}
//		super.contributeNodesToGraph(graph, s, map);
//		super.contributeNodesToGraph(graph, s, map);
		GraphAnimation.recordInitialState(getContentPane());
		Subgraph me = new Subgraph(this, s);
		// me.rowOrder = getActivity().getSortIndex();
		me.outgoingOffset = 5;
		me.incomingOffset = 5;
		IFigure fig = getFigure();
		if (fig instanceof BoxFigure) {
			me.width = fig.getPreferredSize(me.width, me.height).width;
//			int tagHeight = ((SubgraphFigure) fig).getHeader()
//					.getPreferredSize().height;
//			me.height = fig.getPreferredSize().height;
//			me.insets
			int tagHeight = 10;
//			me.insets.top = tagHeight;
//			me.insets.left = 0;
//			me.insets.bottom = tagHeight;
		}
		me.innerPadding = INNER_PADDING;
		me.setPadding(PADDING);
		map.put(this, me);
		graph.nodes.add(me);
		for (int i = 0; i < getChildren().size(); i++) {
			ComponentPart activity = (ComponentPart) getChildren().get(i);
			activity.contributeNodesToGraph(graph, me, map);
		}
	}

}
