package spoon.test.comment.testclasses;

import spoon.SpoonException;
import spoon.support.reflect.reference.CtWildcardStaticTypeMemberReferenceImpl;

/**
 * for more information, please have a look
 * on {@link CtWildcardStaticTypeMemberReferenceImpl}
 */
public class JavadocLinkComment {

    /**
     * Bla
     *
     * @see CtWildcardStaticTypeMemberReferenceImpl FYI
     * @throws SpoonException when something is wrong
     */
    public void method() throws SpoonException {}
}
