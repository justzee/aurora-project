package aurora.ide.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import uncertain.composite.CompositeMap;
import uncertain.composite.QualifiedName;
import uncertain.schema.Attribute;
import uncertain.schema.ComplexType;
import uncertain.schema.Element;
import uncertain.schema.IType;
import aurora.ide.helpers.CompositeMapUtil;
import aurora.ide.helpers.LoadSchemaManager;

@SuppressWarnings("unchecked")
public final class SxsdUtil {
    /**
     * 获取CompositeMap结点声明的所有属性
     * 
     * @param map
     * @return 当没有声明的属性时,返回空的List
     */

    public static List<Attribute> getAttributesNotNull(CompositeMap map) {
        Element ele = LoadSchemaManager.getSchemaManager().getElement(map);
        if (ele == null)
            return new ArrayList<Attribute>();
        List<Attribute> list = ele.getAllAttributes();
        if (list == null)
            return new ArrayList<Attribute>();
        return list;
    }

    /**
     * 照搬CompositeMapUtil中的方法getAvailableChildElements
     * 
     * @param map
     * @return
     */
    private static List<Element> getAvailableChildElements1(CompositeMap map) {
        Element element = LoadSchemaManager.getSchemaManager().getElement(map);
        if (element == null)
            return null;
        List<Element> childElements = new LinkedList<Element>();
        // 判断及节点是否是数组
        if (element.isArray()) {
            IType type = element.getElementType();
            // 如果数组成员类型是元素
            if (type instanceof Element) {
                Element arrayType = LoadSchemaManager.getSchemaManager().getElement(type.getQName());
                childElements.add(arrayType);
            }// 判断数组成员类型是否是基类
            else if (type instanceof ComplexType) {
                childElements.addAll(LoadSchemaManager.getSchemaManager().getElementsOfType(type));
            }
        }
        // 如果节点是元素
        else {
            childElements = getChildElements(map);
        }
        if (childElements != null)
            Collections.sort(childElements);
        return childElements;
    }

    /**
     * 改写CompositeMapUtil中方法getChildElements<br/>
     * 目的:把已经出现的元素也加到最终结果集里面
     * 
     * @param map
     * @return
     */
    private static List<Element> getChildElements(CompositeMap map) {
        Element element = LoadSchemaManager.getSchemaManager().getElement(map);
        Set<?> schemaChilds = CompositeMapUtil.getSchemaChilds(element, LoadSchemaManager.getSchemaManager());
        List<Element> availableChilds = new ArrayList<Element>();

        if (schemaChilds != null) {
            Iterator<?> ite = schemaChilds.iterator();
            while (ite.hasNext()) {
                Object object = ite.next();
                if (!(object instanceof Element))
                    continue;
                Element ele = (Element) object;
                availableChilds.add(ele);
            }
        }
        return availableChilds;
    }

    /**
     * 获取CompositeMap标签下可以存在的标签
     * 
     * @param map
     * @return 非null List
     */
    public static List<Element> getAvailableChildElements(CompositeMap map) {
        List<Element> childs = getAvailableChildElements1(map);
        if (childs == null)
            childs = new ArrayList<Element>();
        Element ele = LoadSchemaManager.getSchemaManager().getElement(map);
        if (ele != null) {
            childs.addAll(ele.getAllArrays());
        }
        Collections.sort(childs);
        return childs;
    }

    /**
     * 计算与CompositeMap相关的提示文档<br/>
     * 包括:属性列表,子节点列表
     * 
     * @param mapNode
     * @return
     *         html代码段形式的文档说明<br/>
     *         并非完整html代码
     */
    public static String getHtmlDocument(CompositeMap mapNode) {
        StringBuilder sb = new StringBuilder(6 * 1024);
        List<Attribute> list = getAttributesNotNull(mapNode);
        if (list.size() == 0) {
            sb.append(String.format("[ %s ] 中没有定义的属性.<br/>", mapNode.getName()));
        } else {
            sb.append(String.format("[ %s ] 中定义过的属性 :<br/><table><tr><th>属性名</th><th>说明</th><th>类型</th></tr>",
                    mapNode.getName()));
            for (Attribute a : list) {
                sb.append(String.format("<tr><td>%s</td><td>%s</td><td>%s</td></tr>", a.getName(),
                        notNull(a.getDocument()), getTypeNameNotNull(a.getAttributeType())));
            }
            sb.append("</table><br/>");
        }
        List<Element> childs = getAvailableChildElements(mapNode);
        if (childs.size() == 0) {
            sb.append(String.format("[ %s ] 中没有定义的子节点.<br/>", mapNode.getName()));
        } else {
            sb.append(String.format("[ %s ] 中定义过的子节点 :<br/><table><tr><th>节点名</th><th>说明</th><th>最大重复次数</th></tr>",
                    mapNode.getName()));
            for (Element e : childs) {
                sb.append(String.format("<tr><td>%s</td><td>%s</td><td>%s</td></tr>", e.getQName().getLocalName(),
                        notNull(e.getDocument()), e.getMaxOccurs() == null ? "∞" : e.getMaxOccurs()));
            }
            sb.append("</table><br/>");
        }
        return sb.toString();
    }

    private static String getTypeNameNotNull(IType type) {
        if (type == null)
            return "";
        QualifiedName qfn = type.getQName();
        if (qfn == null)
            return "";
        String name = qfn.getLocalName();
        if (name == null)
            return "";
        return name;
    }

    private static String notNull(String str) {
        return str == null ? "" : str;
    }
}
