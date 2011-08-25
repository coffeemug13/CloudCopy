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
public class LoggingDateFormatter extends Formatter {

	public LoggingDateFormatter() {
		super();
	}

	@Override
	public String format(LogRecord record) {

		// Zeichenfolgepuffer für formatierten Datensatz erstellen.
		StringBuffer sb = new StringBuffer();
		
		// Mit Datum anfangen.
		// Datum aus dem Protokollsatz abrufen und dem Puffer hinzufügen
//		 Date date = new Date(record.getMillis());
		 sb.append("[" + record.getLevel().getName() + "] ");
		 //SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
		 //sb.append(df.format(new Date(record.getMillis())));
//		 sb.append(" ");
		 sb.append(" " + record.getSourceClassName() + " " + record.getSourceMethodName() + "\n");

		// Versionsnamen abrufen und dem Puffer hinzufügen
//		if (record.getLevel() != Level.FINE) {
		
//		sb.append(": ");
//		}
		// Formatierte Nachricht abrufen (einschließlich Lokalisierung
		// und Substitution von Parametern) und dem Puffer hinzufügen
		if (null != record.getMessage()) sb.append(formatMessage(record) + "\n");
		return sb.toString();
	}
}
