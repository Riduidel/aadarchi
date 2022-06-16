package org.ndx.aadarchi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.ndx.aadarchi.EnhancerLister;
import org.ndx.aadarchi.base.Enhancer;
import org.ndx.aadarchi.base.enhancers.ModelElementAdapter;

import java.util.Arrays;
import java.util.List;

public class EnhancerListerTest {
    @Test
    public void can_create_table_for_a_single_class() {
//        given
        class EmptyEnhancer extends ModelElementAdapter {
            @Override
            public int priority() {
                return 0;
            }
        }
        EnhancerLister tested = new EnhancerLister();
        List<Enhancer> enhancers = Arrays.asList(new EmptyEnhancer());
//        when
        String table = tested.toAsciidocTable(enhancers.stream());
//        then
        Assertions.assertEquals(table, "[cols=\"1,1\"]\n" +
                "|===\n" +
                "|Priority|Enhancer\n" +
                "\n" +
                "|0|EmptyEnhancer\n" +
                "|===");
    }

}
