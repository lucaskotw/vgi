/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import java.util.*;
import vgi.SingleVertexEdgesLayout.Vector2DComparator;

/**
 *
 * @author JLiu
 */
public class WeightedVisibilityGraph extends mxGraph implements Cloneable {

	protected static final boolean IS_ENABLED = true;
	protected static final double VISIBILITY_GRAPH_VERTEX_WDITH = 5;
	protected static final double VISIBILITY_GRAPH_VERTEX_HEIGHT = VISIBILITY_GRAPH_VERTEX_WDITH;
	protected static final double MINIMUM_SPACING = VISIBILITY_GRAPH_VERTEX_WDITH;
	protected static final double CROSSING_COST = 500;

	protected static class LineSegment {

		protected static double SMALL_VALUE = 0.5d;
		double x1;
		double y1;
		double x2;
		double y2;

		public LineSegment(double x1, double y1, double x2, double y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}

		//
		// Based on information from http://local.wasp.uwa.edu.au/~pbourke/geometry/lineline2d/
		//
		public static mxPoint intersection(
				double x1,
				double y1,
				double x2,
				double y2,
				double x3,
				double y3,
				double x4,
				double y4) {

			double denominator = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);

			//
			// If the line segments are not parallel
			//
			if (Math.abs(denominator) >= SMALL_VALUE) {
				double ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / denominator;
				double ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / denominator;

				if ((ua >= 0.0d) && (ua <= 1.0d) && (ub >= 0.0d) && (ub <= 1.0d)) {
					double x = x1 + ua * (x2 - x1);
					double y = y1 + ua * (y2 - y1);
					return new mxPoint(x, y);
				}  // End if ((ua >= 0.0d) && (ua <= 1.0d) && (ub >= 0.0d) && (ub <= 1.0d))

				return null;
			}  // End if (Math.abs(denominator) >= SMALL_VALUE)

			//
			// If the line segments are parallel and not vertical.
			//
			if (Math.abs(x1 - x2) >= SMALL_VALUE) {
				double yIntersect1 = y1 - (y2 - y1) / (x2 - x1) * x1;
				double yIntersect2 = y3 - (y4 - y3) / (x4 - x3) * x3;

				if (Math.abs(yIntersect1 - yIntersect2) >= SMALL_VALUE) {
					return null;
				}

				double min1 = (x1 < x2) ? x1 : x2;
				double max1 = (x1 < x2) ? x2 : x1;
				double min2 = (x3 < x4) ? x3 : x4;
				double max2 = (x3 < x4) ? x4 : x3;
				double leftMin = (min1 < min2) ? min1 : min2;
				double leftMax = (min1 < min2) ? max1 : max2;
				double rightMin = (min1 < min2) ? min2 : min1;
				double rightMax = (min1 < min2) ? max2 : max1;
				if (leftMax < rightMin) {
					return null;
				}

				double x;
				if (leftMax < rightMax) {
					x = (leftMax + rightMin) / 2;
				} else {
					x = (rightMin + rightMax) / 2;
				}
				double y = (y2 - y1) / (x2 - x1) * x + yIntersect1;

				return new mxPoint(x, y);
			}  // End if (Math.abs(x1 - x2) >= SMALL_VALUE)

			//
			// If the line segments are parallel and vertical.
			//
			if (Math.abs(x1 - x3) >= SMALL_VALUE) {
				return null;
			}

			double min1 = (y1 < y2) ? y1 : y2;
			double max1 = (y1 < y2) ? y2 : y1;
			double min2 = (y3 < y4) ? y3 : y4;
			double max2 = (y3 < y4) ? y4 : y3;
			double leftMin = (min1 < min2) ? min1 : min2;
			double leftMax = (min1 < min2) ? max1 : max2;
			double rightMin = (min1 < min2) ? min2 : min1;
			double rightMax = (min1 < min2) ? max2 : max1;
			if (leftMax < rightMin) {
				return null;
			}

			double y;
			if (leftMax < rightMax) {
				y = (leftMax + rightMin) / 2;
			} else {
				y = (rightMin + rightMax) / 2;
			}

			return new mxPoint(x1, y);
		}  // End public static mxPoint intersection(...)
	}  // End protected static class LineSegment
	protected List<mxICell> roadblocks;
//	protected List<mxICell> hindrances;
	protected Map<mxICell, List<mxICell>> obstacleToVerticesMap;
	protected Map<mxICell, List<LineSegment>> hindranceToLineSegmentsMap;

	protected final void inititializeMembers() {
		this.roadblocks = new LinkedList<mxICell>();
//		this.hindrances = new LinkedList<mxICell>();
		this.obstacleToVerticesMap = new HashMap<mxICell, List<mxICell>>();
		this.hindranceToLineSegmentsMap = new HashMap<mxICell, List<LineSegment>>();
	}  // End protected final void inititializeMembers()

	public WeightedVisibilityGraph(mxIGraphModel model, mxStylesheet stylesheet) {
		super(model, stylesheet);
		inititializeMembers();
	}

	public WeightedVisibilityGraph(mxStylesheet stylesheet) {
		super(stylesheet);
		inititializeMembers();
	}

	public WeightedVisibilityGraph(mxIGraphModel model) {
		super(model);
		inititializeMembers();
	}

	public WeightedVisibilityGraph() {
		super();
		inititializeMembers();
	}

	@Override
	public Object clone() {
		WeightedVisibilityGraph clone = new WeightedVisibilityGraph();
		clone.roadblocks = new LinkedList<mxICell>(this.roadblocks);
		clone.obstacleToVerticesMap = new HashMap<mxICell, List<mxICell>>(this.obstacleToVerticesMap);
		clone.hindranceToLineSegmentsMap = new HashMap<mxICell, List<LineSegment>>(this.hindranceToLineSegmentsMap);
		Object objects[] = this.getChildCells(this.getDefaultParent());
		clone.addCells(objects);
		return clone;
	}  // End public Object clone() throws CloneNotSupportedException

	public Map<mxICell, List<mxICell>> getObstacleToVerticesMap() {
		return this.obstacleToVerticesMap;
	}

	protected void addEdgesForVertex(mxICell vertex) {

		if (!IS_ENABLED) {
			return;
		}
		if (vertex == null) {
			throw new IllegalArgumentException("Input 'vertex' is null.");
		}
		mxGeometry geometry = vertex.getGeometry();
		if (geometry == null) {
			throw new IllegalArgumentException("Input 'vertex' has null geometry.");
		}

		Object parent = this.getDefaultParent();
		Object objects[] = this.getChildVertices(parent);

		for (int index = 0; index < objects.length; index++) {

			if (objects[index] == vertex) {
				continue;
			}
			if (!(objects[index] instanceof mxICell)) {
				throw new IllegalStateException("A vertex is not of the type mxICell.");
			}
			mxICell anotherVertex = (mxICell) objects[index];
			mxGeometry anotherGeometry = anotherVertex.getGeometry();
			if (anotherGeometry == null) {
				throw new IllegalStateException("The 'anotherVertex' variable has null geometry.");
			}

			boolean isVisible = true;
			Iterator<mxICell> iterateCells = this.roadblocks.iterator();
			while (iterateCells.hasNext()) {

				mxICell roadblock = iterateCells.next();
				mxGeometry roadblockGeometry = roadblock.getGeometry();
				if (roadblockGeometry == null) {
					throw new IllegalStateException("The 'roadblock' variable has null geometry.");
				}
				mxPoint point = roadblockGeometry.intersectLine(
						geometry.getCenterX(),
						geometry.getCenterY(),
						anotherGeometry.getCenterX(),
						anotherGeometry.getCenterY());
				if (point != null) {
					isVisible = false;
					break;
				}

			}  // End while (iterateCells.hasNext())

			if (!isVisible) {
				continue;
			}

			double crossingNumber = 0.0d;

			for (mxICell cell : this.hindranceToLineSegmentsMap.keySet()) {

				List<LineSegment> lineSegments = this.hindranceToLineSegmentsMap.get(cell);
				for (LineSegment lineSegment : lineSegments) {

					mxPoint intersection = LineSegment.intersection(
							geometry.getCenterX(),
							geometry.getCenterY(),
							anotherGeometry.getCenterX(),
							anotherGeometry.getCenterY(),
							lineSegment.x1,
							lineSegment.y1,
							lineSegment.x2,
							lineSegment.y2);
					if (intersection == null) {
						continue;
					}
					if (((intersection.getX() == geometry.getCenterX()) && (intersection.getY() == geometry.getCenterY()))
							|| ((intersection.getX() == anotherGeometry.getCenterX()) && (intersection.getY() == anotherGeometry.getCenterY()))) {
						if (((intersection.getX() == lineSegment.x1) && (intersection.getY() == lineSegment.y1))
								|| ((intersection.getX() == lineSegment.x2) && (intersection.getY() == lineSegment.y2))) {
							crossingNumber = crossingNumber + 0.25;
						} else {
							crossingNumber = crossingNumber + 0.5;
						}
					} else {
						if (((intersection.getX() == lineSegment.x1) && (intersection.getY() == lineSegment.y1))
								|| ((intersection.getX() == lineSegment.x2) && (intersection.getY() == lineSegment.y2))) {
							crossingNumber = crossingNumber + 0.5;
						} else {
							crossingNumber = crossingNumber + 1;
						}
					}

				}  // End for (LineSegment lineSegment : lineSegments)

			}  // End for (mxICell cell : this.hindranceToLineSegmentsMap.keySet())

			double cost = 1 + Vector2D.length(
					geometry.getCenterX() - anotherGeometry.getCenterX(),
					geometry.getCenterY() - anotherGeometry.getCenterY())
					+ CROSSING_COST * crossingNumber;
			this.insertEdge(
					parent,
					null,
					cost,
					vertex,
					anotherVertex);

		}  // End for (int index = 0; index < objects.length; index++)

	}  // End public void addEdgesForVertex(mxICell vertex)

	public void addRoadblock(mxICell roadblock) {

		if (!IS_ENABLED) {
			return;
		}
		if ((roadblock == null) || (!(roadblock.isVertex()))) {
			throw new IllegalArgumentException("Input 'roadblock' is null or not a vertex.");
		}
		mxGeometry geometry = roadblock.getGeometry();
		if (geometry == null) {
			throw new IllegalArgumentException("Input 'roadblock' has null geometry.");
		}

		Object parent = this.getDefaultParent();
		Object objects[] = this.getChildEdges(parent);

		for (int index = 0; index < objects.length; index++) {

			if (!(objects[index] instanceof mxICell)) {
				throw new IllegalStateException("A vertex is not of the type mxICell.");
			}
			mxICell edge = (mxICell) objects[index];
			if (!(edge.isEdge())) {
				throw new IllegalStateException("The 'edge' variable is not an edge.");
			}
			mxICell source = edge.getTerminal(true);
			if (source == null) {
				throw new IllegalStateException("The 'edge' variable has null source.");
			}
			mxGeometry sourceGeometry = source.getGeometry();
			if (sourceGeometry == null) {
				throw new IllegalStateException("The 'source' variable has null geometry.");
			}
			mxICell target = edge.getTerminal(false);
			if (target == null) {
				throw new IllegalStateException("The 'edge' variable has null target.");
			}
			mxGeometry targetGeometry = target.getGeometry();
			if (targetGeometry == null) {
				throw new IllegalStateException("The 'target' variable has null geometry.");
			}

			mxPoint point = geometry.intersectLine(
					sourceGeometry.getCenterX(),
					sourceGeometry.getCenterY(),
					targetGeometry.getCenterX(),
					targetGeometry.getCenterY());
			if (point != null) {
				Object cells[] = {edge};
				this.removeCells(cells);
			}

		}  // End for (int index = 0; index < objects.length; index++)

		this.roadblocks.add(roadblock);
		List<mxICell> vertices = new LinkedList<mxICell>();
		mxICell vertex = (mxICell) this.insertVertex(
				parent,
				null,
				null,
				geometry.getX() - MINIMUM_SPACING - VISIBILITY_GRAPH_VERTEX_WDITH / 2,
				geometry.getY() - MINIMUM_SPACING - VISIBILITY_GRAPH_VERTEX_HEIGHT / 2,
				VISIBILITY_GRAPH_VERTEX_WDITH,
				VISIBILITY_GRAPH_VERTEX_HEIGHT);
		this.addEdgesForVertex(vertex);
		vertices.add(vertex);
		vertex = (mxICell) this.insertVertex(
				parent,
				null,
				null,
				geometry.getX() + geometry.getWidth() + MINIMUM_SPACING - VISIBILITY_GRAPH_VERTEX_WDITH / 2,
				geometry.getY() - MINIMUM_SPACING - VISIBILITY_GRAPH_VERTEX_HEIGHT / 2,
				VISIBILITY_GRAPH_VERTEX_WDITH,
				VISIBILITY_GRAPH_VERTEX_HEIGHT);
		this.addEdgesForVertex(vertex);
		vertices.add(vertex);
		vertex = (mxICell) this.insertVertex(
				parent,
				null,
				null,
				geometry.getX() + geometry.getWidth() + MINIMUM_SPACING - VISIBILITY_GRAPH_VERTEX_WDITH / 2,
				geometry.getY() + geometry.getHeight() + MINIMUM_SPACING - VISIBILITY_GRAPH_VERTEX_HEIGHT / 2,
				VISIBILITY_GRAPH_VERTEX_WDITH,
				VISIBILITY_GRAPH_VERTEX_HEIGHT);
		this.addEdgesForVertex(vertex);
		vertices.add(vertex);
		vertex = (mxICell) this.insertVertex(
				parent,
				null,
				null,
				geometry.getX() - MINIMUM_SPACING - VISIBILITY_GRAPH_VERTEX_WDITH / 2,
				geometry.getY() + geometry.getHeight() + MINIMUM_SPACING - VISIBILITY_GRAPH_VERTEX_HEIGHT / 2,
				VISIBILITY_GRAPH_VERTEX_WDITH,
				VISIBILITY_GRAPH_VERTEX_HEIGHT);
		this.addEdgesForVertex(vertex);
		vertices.add(vertex);
		this.obstacleToVerticesMap.put(roadblock, vertices);
		vertices = null;  // List<mxICell> vertices = new LinkedList<mxICell>();

	}  // End public void addRoadblock(mxICell roadblock)

	public void addHindrance(mxICell hindrance) {

		if (!IS_ENABLED) {
			return;
		}
		if ((hindrance == null) || (!(hindrance.isEdge()))) {
			throw new IllegalArgumentException("Input 'hindrance' is null or not an edge.");
		}
		mxGeometry geometry = hindrance.getGeometry();
		if (geometry == null) {
			throw new IllegalArgumentException("Input 'hindrance' has null geometry.");
		}
		mxICell source = hindrance.getTerminal(true);
		mxPoint sourcePoint = null;
		if (source == null) {
			sourcePoint = geometry.getSourcePoint();
		} else {
			mxGeometry sourceGeometry = source.getGeometry();
			if (sourceGeometry == null) {
				throw new IllegalStateException("The 'source' variable has null geometry.");
			}
			sourcePoint = new mxPoint(sourceGeometry.getCenterX(), sourceGeometry.getCenterY());
		}
		mxICell target = hindrance.getTerminal(false);
		mxPoint targetPoint = null;
		if (target == null) {
			targetPoint = geometry.getTargetPoint();
		} else {
			mxGeometry targetGeometry = target.getGeometry();
			if (targetGeometry == null) {
				throw new IllegalStateException("The 'target' variable has null geometry.");
			}
			targetPoint = new mxPoint(targetGeometry.getCenterX(), targetGeometry.getCenterY());
		}
		if ((sourcePoint == null) || (targetPoint == null)) {
			throw new IllegalStateException("The 'sourcePoint' or 'targetPoint' variable is null.");
		}

		List<LineSegment> lineSegments = new LinkedList<LineSegment>();
		List<mxPoint> newVerticesPositions = new LinkedList<mxPoint>();
		mxPoint previousPoint = sourcePoint;
		Vector2D previousVector = null;
		List<mxPoint> allPoints = new LinkedList<mxPoint>();
		List<mxPoint> points = geometry.getPoints();
		if (points != null) {
			allPoints.addAll(points);
		}
		points = null;  // List<mxPoint> points = geometry.getPoints();
		allPoints.add(targetPoint);

		for (mxPoint point : allPoints) {

			LineSegment lineSegment = new LineSegment(
					previousPoint.getX(),
					previousPoint.getY(),
					point.getX(),
					point.getY());
			lineSegments.add(lineSegment);
			lineSegment = null; // LineSegment lineSegment = new LineSegment(...)
			Vector2D vector = Vector2D.subtract(
					point.getX(),
					point.getY(),
					previousPoint.getX(),
					previousPoint.getY());
			if (previousVector != null) {
				Vector2D externalBisector = previousVector.unitVector().
						subtract(vector.unitVector()).
						scalarProduct(MINIMUM_SPACING);
				newVerticesPositions.add(new mxPoint(
						previousPoint.getX() + externalBisector.getX(),
						previousPoint.getY() + externalBisector.getY()));
			}  // End if (previousVector != null)

			previousPoint = point;
			previousVector = vector;

		}  // End for (mxPoint point : allPoints)

		this.hindranceToLineSegmentsMap.put(hindrance, lineSegments);
		Object parent = this.getDefaultParent();
		Object objects[] = this.getChildEdges(parent);

		for (int index = 0; index < objects.length; index++) {

			if (!(objects[index] instanceof mxICell)) {
				throw new IllegalStateException("A vertex is not of the type mxICell.");
			}
			mxICell edge = (mxICell) objects[index];
			if (!(edge.isEdge())) {
				throw new IllegalStateException("The 'edge' variable is not an edge.");
			}
			source = edge.getTerminal(true);
			if (source == null) {
				throw new IllegalStateException("The 'edge' variable has null source.");
			}
			mxGeometry sourceGeometry = source.getGeometry();
			if (sourceGeometry == null) {
				throw new IllegalStateException("The 'source' variable has null geometry.");
			}
			target = edge.getTerminal(false);
			if (target == null) {
				throw new IllegalStateException("The 'edge' variable has null target.");
			}
			mxGeometry targetGeometry = target.getGeometry();
			if (targetGeometry == null) {
				throw new IllegalStateException("The 'target' variable has null geometry.");
			}
			Object object = edge.getValue();
			if (!(object instanceof Number)) {
				throw new IllegalStateException("The 'edge' variable's value is not of the type Number.");
			}
			double cost = ((Number) object).doubleValue();
			double crossingNumber = 0.0d;

			Iterator<LineSegment> iterateLineSegments = lineSegments.iterator();
			while (iterateLineSegments.hasNext()) {

				LineSegment lineSegment = iterateLineSegments.next();
				mxPoint intersection = LineSegment.intersection(
						sourceGeometry.getCenterX(),
						sourceGeometry.getCenterY(),
						targetGeometry.getCenterX(),
						targetGeometry.getCenterY(),
						lineSegment.x1,
						lineSegment.y1,
						lineSegment.x2,
						lineSegment.y2);
				if (intersection == null) {
					continue;
				}
				if (((intersection.getX() == sourceGeometry.getCenterX()) && (intersection.getY() == sourceGeometry.getCenterY()))
						|| ((intersection.getX() == targetGeometry.getCenterX()) && (intersection.getY() == targetGeometry.getCenterY()))) {
					if (((intersection.getX() == lineSegment.x1) && (intersection.getY() == lineSegment.y1))
							|| ((intersection.getX() == lineSegment.x2) && (intersection.getY() == lineSegment.y2))) {
						crossingNumber = crossingNumber + 0.25;
					} else {
						crossingNumber = crossingNumber + 0.5;
					}
				} else {
					if (((intersection.getX() == lineSegment.x1) && (intersection.getY() == lineSegment.y1))
							|| ((intersection.getX() == lineSegment.x2) && (intersection.getY() == lineSegment.y2))) {
						crossingNumber = crossingNumber + 0.5;
					} else {
						crossingNumber = crossingNumber + 1;
					}
				}

			}  // End while (iterateLineSegments.hasNext())

			if (crossingNumber > 0.0d) {
				cost = cost + CROSSING_COST * crossingNumber;
				edge.setValue(cost);
			}

		}  // End for (int index = 0; index < objects.length; index++)

		lineSegments = null;  // List<LineSegment> lineSegments = new LinkedList<LineSegment>();
		List<mxICell> vertices = new LinkedList<mxICell>();
		Iterator<mxPoint> iteratePositions = newVerticesPositions.iterator();
		while (iteratePositions.hasNext()) {

			mxPoint position = iteratePositions.next();
			mxICell vertex = (mxICell) this.insertVertex(
					parent,
					null,
					null,
					position.getX() - VISIBILITY_GRAPH_VERTEX_WDITH / 2,
					position.getY() - VISIBILITY_GRAPH_VERTEX_HEIGHT / 2,
					VISIBILITY_GRAPH_VERTEX_WDITH,
					VISIBILITY_GRAPH_VERTEX_HEIGHT);
			this.addEdgesForVertex(vertex);
			vertices.add(vertex);

		}  // End while (iteratePositions.hasNext())

		if (!(vertices.isEmpty())) {
			this.obstacleToVerticesMap.put(hindrance, vertices);
		}
		vertices = null;  // List<mxICell> vertices = new LinkedList<mxICell>();
		newVerticesPositions = null;  // List<mxPoint> newVerticesPositions = new LinkedList<mxPoint>();

	}  // public void addHindrance(mxICell hindrance)

	public void addVerticesIntoOutOf(mxICell roadblock) {

		if (!IS_ENABLED) {
			return;
		}
		if ((roadblock == null) || (!(roadblock.isVertex()))) {
			throw new IllegalArgumentException("Input 'roadblock' is null or not a vertex.");
		}
		if (!(this.roadblocks.contains(roadblock))) {
			throw new IllegalArgumentException("Input 'roadblock' must be already added to this weighted visibility graph.");
		}
		mxGeometry geometry = roadblock.getGeometry();
		if (geometry == null) {
			throw new IllegalArgumentException("Input 'roadblock' has null geometry.");
		}

		int edgeCount = roadblock.getEdgeCount();
		List<Vector2D> edgeUnitVectorsList = new ArrayList<Vector2D>(edgeCount);

		for (int index = 0; index < edgeCount; index++) {
			mxICell edge = roadblock.getEdgeAt(index);
			if ((edge == null) || (!(edge.isEdge()))) {
				throw new IllegalStateException("The 'edge' variable is null or not an edge.");
			}
			mxGeometry edgeGeometry = edge.getGeometry();
			if (edgeGeometry == null) {
				throw new IllegalStateException("The 'edge' variable has null geometry.");
			}
			List<mxPoint> points = edgeGeometry.getPoints();
			mxICell source = edge.getTerminal(true);
			mxICell target = edge.getTerminal(false);
			mxPoint point;
			if (roadblock == source) {
				if ((points == null) || (points.isEmpty())) {
					if (target == null) {
						point = edgeGeometry.getTargetPoint();
					} else {
						mxGeometry targetGeometry = target.getGeometry();
						point = new mxPoint(targetGeometry.getCenterX(), targetGeometry.getCenterY());
					}
				} else {
					point = points.get(0);
				}
			} else if (roadblock == target) {
				if ((points == null) || (points.isEmpty())) {
					if (source == null) {
						point = edgeGeometry.getSourcePoint();
					} else {
						mxGeometry sourceGeometry = source.getGeometry();
						point = new mxPoint(sourceGeometry.getCenterX(), sourceGeometry.getCenterY());
					}
				} else {
					point = points.get(points.size() - 1);
				}
			} else {
				throw new IllegalStateException("Edge does not connect to roadblock.");
			}
			edgeUnitVectorsList.add(Vector2D.subtract(point.getX(), point.getY(),
					geometry.getCenterX(), geometry.getCenterY()).unitVector());
		}  // End for (int index = 0; index < edgeCount; index++)

		double x = geometry.getWidth() / 2 + MINIMUM_SPACING;
		double y = geometry.getHeight() / 2 + MINIMUM_SPACING;
		double radius = Math.sqrt(x * x + y * y);

		Collections.sort(edgeUnitVectorsList, new Vector2DComparator());
		List<mxPoint> newVerticesPositions = new LinkedList<mxPoint>();

		for (int index = 0; index < edgeCount; index++) {

			int nextIndex = (index + 1 >= edgeCount) ? (0) : (index + 1);
			Vector2D currentVector = edgeUnitVectorsList.get(index);
			Vector2D nextVector = edgeUnitVectorsList.get(nextIndex);
			if (currentVector.isParallel(nextVector)) {
				continue;
			}
			Vector2D rightUnitVector = currentVector.rotate90DegreesPositively();
			if (currentVector.isAntiParallel(nextVector)) {
				newVerticesPositions.add(new mxPoint(
						geometry.getCenterX() + radius * rightUnitVector.getX(),
						geometry.getCenterY() + radius * rightUnitVector.getY()));
				continue;
			}
			Vector2D bisector = currentVector.add(nextVector);
			//
			// if the next vecotr points to the left of the current vector
			//
			if (nextVector.dotProduct(rightUnitVector) < 0) {
				bisector = bisector.reverse();
			}
			double bisectorLength = bisector.length();
			newVerticesPositions.add(new mxPoint(
					geometry.getCenterX() + radius * bisector.getX() / bisectorLength,
					geometry.getCenterY() + radius * bisector.getY() / bisectorLength));

		}  // End for (int index = 0; index < edgeCount; index++)

		edgeUnitVectorsList = null;  // List<Vector2D> edgeUnitVectorsList = new ArrayList<Vector2D>(edgeCount);

		Object parent = this.getDefaultParent();
		List<mxICell> vertices = new LinkedList<mxICell>();
		Iterator<mxPoint> iteratePositions = newVerticesPositions.iterator();
		while (iteratePositions.hasNext()) {

			mxPoint position = iteratePositions.next();
			mxICell vertex = (mxICell) this.insertVertex(
					parent,
					null,
					null,
					position.getX() - VISIBILITY_GRAPH_VERTEX_WDITH / 2,
					position.getY() - VISIBILITY_GRAPH_VERTEX_HEIGHT / 2,
					VISIBILITY_GRAPH_VERTEX_WDITH,
					VISIBILITY_GRAPH_VERTEX_HEIGHT);
			this.addEdgesForVertex(vertex);
			vertices.add(vertex);

		}  // End while (iteratePositions.hasNext())

		if (!(vertices.isEmpty())) {
			List<mxICell> moreVertices = this.obstacleToVerticesMap.get(roadblock);
			moreVertices.addAll(vertices);
//			this.obstacleToVerticesMap.put(roadblock, moreVertices);
		}
		vertices = null;  // List<mxICell> vertices = new LinkedList<mxICell>();
		newVerticesPositions = null;  // List<mxPoint> newVerticesPositions = new LinkedList<mxPoint>();

	}  // End public void addVerticesIntoOutOf(mxICell roadblock)
}  // End public class WeightedVisibilityGraph extends mxGraph implements Cloneable
