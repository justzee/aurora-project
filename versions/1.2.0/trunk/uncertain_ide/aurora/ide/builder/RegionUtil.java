package aurora.ide.builder;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

public class RegionUtil {
	/**
	 * 判断offset是否在Region中
	 * 
	 * @param region
	 * @param offset
	 * @return
	 */
	public static boolean isInRegion(IRegion region, int offset) {
		return offset >= region.getOffset()
				&& offset <= region.getOffset() + region.getLength();
	}

	/**
	 * 判断region2是否被region1完整包含
	 * 
	 * @param region1
	 * @param region2
	 * @return
	 */
	public static boolean isSubRegion(IRegion region1, IRegion region2) {
		return isInRegion(region1, region2.getOffset())
				&& isInRegion(region1,
						region2.getOffset() + region2.getLength());
	}

	/**
	 * 计算两个Region的交集,如果不相交则返回null
	 * 
	 * @param region1
	 * @param region2
	 * @return
	 */
	public static IRegion intersect(IRegion region1, IRegion region2) {
		int offset = Math.max(region1.getOffset(), region2.getOffset());
		int offset2 = Math.min(region1.getOffset() + region1.getLength(),
				region2.getOffset() + region2.getLength());
		if (offset2 < offset)
			return null;
		IRegion region = new Region(offset, offset2 - offset);
		return region;
	}

	/**
	 * 计算并集(并非数学意义上的并集)
	 * 
	 * @param region1
	 * @param region2
	 * @return
	 */
	public static IRegion union(IRegion region1, IRegion region2) {
		int offset = Math.min(region1.getOffset(), region2.getOffset());
		int offset2 = Math.max(region1.getOffset() + region1.getLength(),
				region2.getOffset() + region2.getLength());
		IRegion region = new Region(offset, offset2 - offset);
		return region;
	}
}
