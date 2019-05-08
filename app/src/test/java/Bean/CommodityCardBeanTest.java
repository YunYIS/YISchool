package Bean;

import org.junit.Test;

import Bean.jsonBean.CommodityCardBean;

import static org.junit.Assert.*;

/**
 * 测试CommodityCardBean类重写的hashCode和equals方法
 */
public class CommodityCardBeanTest {

    private CommodityCardBean c1 = new CommodityCardBean();
    private CommodityCardBean c2 = new CommodityCardBean();
    private CommodityCardBean c3 = new CommodityCardBean();

    @Test
    public void testHashCode() {
        c1.setPrice(34.5988);
        c2.setPrice(34.5989);
        c3.setPrice(34.598900);
        int h1 = c1.hashCode();
        int h2 = c2.hashCode();
        int h3 = c3.hashCode();
        assertNotEquals(h1, h2);
        assertEquals(h2, h3);
        assertNotEquals(h1, h3);
    }
    @Test
    public void testEquals(){
        c1.setPrice(34.5988);
        c2.setPrice(34.5989);
        c3.setPrice(34.598900);
        assertEquals(false, c1.equals(c2));
        assertEquals(true, c2.equals(c3));
        assertEquals(false, c1.equals(c3));

        assertEquals(false, c2.equals(c1));
        assertEquals(true, c3.equals(c2));
        assertEquals(false, c3.equals(c1));
    }
}