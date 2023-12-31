package com.springframework.util.xml;

import com.springframework.util.Assert;
import com.sun.istack.internal.Nullable;
import org.w3c.dom.*;
import org.xml.sax.ContentHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class DomUtils {

    /**
     * Retrieves all child elements of the given DOM element that match any of the given element names.
     * Only looks at the direct child level of the given element; do not go into further depth
     * (in contrast to the DOM API's {@code getElementsByTagName} method).
     *
     * @param ele           the DOM element to analyze
     * @param childEleNames the child element names to look for
     * @return a List of child {@code org.w3c.dom.Element} instances
     * @see org.w3c.dom.Element
     * @see org.w3c.dom.Element#getElementsByTagName
     */
    public static List<Element> getChildElementsByTagName(Element ele, String... childEleNames) {
        Assert.notNull(ele, "Element must not be null");
        Assert.notNull(childEleNames, "Element names collection must not be null");
        List<String> childEleNameList = Arrays.asList(childEleNames);
        NodeList nl = ele.getChildNodes();
        List<Element> childEles = new ArrayList<>();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element && nodeNameMatch(node, childEleNameList)) {
                childEles.add((Element) node);
            }
        }
        return childEles;
    }

    /**
     * Retrieves all child elements of the given DOM element that match the given element name.
     * Only look at the direct child level of the given element; do not go into further depth
     * (in contrast to the DOM API's {@code getElementsByTagName} method).
     *
     * @param ele          the DOM element to analyze
     * @param childEleName the child element name to look for
     * @return a List of child {@code org.w3c.dom.Element} instances
     * @see org.w3c.dom.Element
     * @see org.w3c.dom.Element#getElementsByTagName
     */
    public static List<Element> getChildElementsByTagName(Element ele, String childEleName) {
        return getChildElementsByTagName(ele, new String[]{childEleName});
    }

    /**
     * Utility method that returns the first child element identified by its name.
     *
     * @param ele          the DOM element to analyze
     * @param childEleName the child element name to look for
     * @return the {@code org.w3c.dom.Element} instance, or {@code null} if none found
     */
    @Nullable
    public static Element getChildElementByTagName(Element ele, String childEleName) {
        Assert.notNull(ele, "Element must not be null");
        Assert.notNull(childEleName, "Element name must not be null");
        NodeList nl = ele.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element && nodeNameMatch(node, childEleName)) {
                return (Element) node;
            }
        }
        return null;
    }

    /**
     * Utility method that returns the first child element value identified by its name.
     *
     * @param ele          the DOM element to analyze
     * @param childEleName the child element name to look for
     * @return the extracted text value, or {@code null} if no child element found
     */
    @Nullable
    public static String getChildElementValueByTagName(Element ele, String childEleName) {
        Element child = getChildElementByTagName(ele, childEleName);
        return (child != null ? getTextValue(child) : null);
    }

    /**
     * Retrieves all child elements of the given DOM element.
     *
     * @param ele the DOM element to analyze
     * @return a List of child {@code org.w3c.dom.Element} instances
     */
    public static List<Element> getChildElements(Element ele) {
        Assert.notNull(ele, "Element must not be null");
        NodeList nl = ele.getChildNodes();
        List<Element> childEles = new ArrayList<>();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element) {
                childEles.add((Element) node);
            }
        }
        return childEles;
    }

    /**
     * Extracts the text value from the given DOM element, ignoring XML comments.
     * <p>Appends all CharacterData nodes and EntityReference nodes into a single
     * String value, excluding Comment nodes. Only exposes actual user-specified
     * text, no default values of any kind.
     *
     * @see CharacterData
     * @see EntityReference
     * @see Comment
     */
    public static String getTextValue(Element valueEle) {
        Assert.notNull(valueEle, "Element must not be null");
        StringBuilder sb = new StringBuilder();
        NodeList nl = valueEle.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node item = nl.item(i);
            if ((item instanceof CharacterData && !(item instanceof Comment)) || item instanceof EntityReference) {
                sb.append(item.getNodeValue());
            }
        }
        return sb.toString();
    }

    /**
     * Namespace-aware equals comparison. Returns {@code true} if either
     * {@link Node#getLocalName} or {@link Node#getNodeName} equals
     * {@code desiredName}, otherwise returns {@code false}.
     */
    public static boolean nodeNameEquals(Node node, String desiredName) {
        Assert.notNull(node, "Node must not be null");
        Assert.notNull(desiredName, "Desired name must not be null");
        return nodeNameMatch(node, desiredName);
    }

    /**
     * Returns a SAX {@code ContentHandler} that transforms callback calls to DOM {@code Node}s.
     *
     * @param node the node to publish events to
     * @return the content handler
     */
    public static ContentHandler createContentHandler(Node node) {
        return new DomContentHandler(node);
    }

    /**
     * Matches the given node's name and local name against the given desired name.
     */
    private static boolean nodeNameMatch(Node node, String desiredName) {
        return (desiredName.equals(node.getNodeName()) || desiredName.equals(node.getLocalName()));
    }

    /**
     * Matches the given node's name and local name against the given desired names.
     */
    private static boolean nodeNameMatch(Node node, Collection<?> desiredNames) {
        return (desiredNames.contains(node.getNodeName()) || desiredNames.contains(node.getLocalName()));
    }

}
