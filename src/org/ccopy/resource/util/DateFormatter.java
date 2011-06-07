/**
 * 
 */
package org.ccopy.resource.util;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * MeinCustomFormatter formatiert den Protokollsatz wie folgt: Datum Version
 * lokalisierte Nachricht mit Parametern
 */
public class DateFormatter extends Formatter {

	public DateFormatter() {
		super();
	}

	public String format(LogRecord record) {

		// Zeichenfolgepuffer für formatierten Datensatz erstellen.
		// Mit Datum anfangen.
		StringBuffer sb = new StringBuffer();

		// Datum aus dem Protokollsatz abrufen und dem Puffer hinzufügen
		// Date date = new Date(record.getMillis());
		// sb.append(date.toString());
		// sb.append(" ");

		// Versionsnamen abrufen und dem Puffer hinzufügen
//		if (record.getLevel() != Level.FINE) {
		sb.append(record.getLevel().getName());
		sb.append(": ");
//		}
		// Formatierte Nachricht abrufen (einschließlich Lokalisierung
		// und Substitution von Parametern) und dem Puffer hinzufügen
		sb.append(formatMessage(record));
		sb.append("\n");
		return sb.toString();
	}
}
