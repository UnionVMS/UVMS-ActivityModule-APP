package eu.europa.ec.fisheries.ers.fa.entities;

import org.hibernate.cfg.ImprovedNamingStrategy;

/**
 * A custom naming strategy implementation which uses following naming conventions:
 * <ul>
 *     <li>Table names are lower case and in plural form. Words are separated with '_' character.</li>
 *     <li>Column names are lower case and words are separated with '_' character.</li>
 * </ul>
 * @author Petri Kainulainen
 */
public class CustomNamingStrategy extends ImprovedNamingStrategy {

    private static final String PLURAL_SUFFIX = "s";

    /**
     * Transforms class names to table names by using the described naming conventions.
     * @param className
     * @return  The constructed table name.
     */
    @Override
    public String classToTableName(String className) {
        String tableNameInSingularForm = super.classToTableName(className);
        return transformToPluralForm(tableNameInSingularForm);
    }

    private String transformToPluralForm(String tableNameInSingularForm) {
        StringBuilder pluralForm = new StringBuilder();

        pluralForm.append(tableNameInSingularForm);
        pluralForm.append(PLURAL_SUFFIX);

        return pluralForm.toString();
    }
}