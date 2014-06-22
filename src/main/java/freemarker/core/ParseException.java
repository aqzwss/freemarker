/*
 * Copyright 2014 Attila Szegedi, Daniel Dekany, Jonathan Revusky
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package freemarker.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.utility.SecurityUtilities;
import freemarker.template.utility.StringUtil;

/**
 * Parsing-time exception in a template (as opposed to runtime: {@link TemplateException}). This usually signals
 * syntactical/lexical errors.
 * 
 * Note that on JavaCC-level lexical errors throw {@link TokenMgrError} instead of this, however with the API-s that
 * most users use those will be wrapped into {@link ParseException}-s. 
 *
 * This is a modified version of file generated by JavaCC from FTL.jj.
 * You can modify this class to customize the error reporting mechanisms so long as the public interface
 * remains compatible with the original.
 * 
 * @see TokenMgrError
 */
public class ParseException extends java.io.IOException implements FMParserConstants {

    /**
     * This is the last token that has been consumed successfully.  If
     * this object has been created due to a parse error, the token
     * following this token will (therefore) be the first error token.
     */
    public Token currentToken;

    private static volatile Boolean jbossToolsMode;

    private boolean messageAndDescriptionRendered;
    private String message;
    private String description; 

    public int columnNumber, lineNumber;
    public int endColumnNumber, endLineNumber;

    /**
     * Each entry in this array is an array of integers.  Each array
     * of integers represents a sequence of tokens (by their ordinal
     * values) that is expected at this point of the parse.
     */
    public int[][] expectedTokenSequences;

    /**
     * This is a reference to the "tokenImage" array of the generated
     * parser within which the parse error occurred.  This array is
     * defined in the generated ...Constants interface.
     */
    public String[] tokenImage;

    /**
     * The end of line string for this machine.
     */
    protected String eol = SecurityUtilities.getSystemProperty("line.separator", "\n");

    /** @deprecated Will be remove without replacement in 2.4. */
    protected boolean specialConstructor;  

    // This was no part of Throwable on J2SE 1.2; remove in Java 5
    private final Throwable cause;

    private String templateName;

    /**
     * This constructor is used by the method "generateParseException"
     * in the generated parser.  Calling this constructor generates
     * a new object of this type with the fields "currentToken",
     * "expectedTokenSequences", and "tokenImage" set.
     * This constructor calls its super class with the empty string
     * to force the "toString" method of parent class "Throwable" to
     * print the error message in the form:
     *     ParseException: <result of getMessage>
     */
    public ParseException(Token currentTokenVal,
            int[][] expectedTokenSequencesVal,
            String[] tokenImageVal
            )
    {
        super("");
        cause = null;
        currentToken = currentTokenVal;
        specialConstructor = true;
        expectedTokenSequences = expectedTokenSequencesVal;
        tokenImage = tokenImageVal;
        lineNumber = currentToken.next.beginLine;
        columnNumber = currentToken.next.beginColumn;
        endLineNumber = currentToken.next.endLine;
        endColumnNumber = currentToken.next.endColumn;
    }

    /**
     * The following constructors are for use by you for whatever
     * purpose you can think of.  Constructing the exception in this
     * manner makes the exception behave in the normal way - i.e., as
     * documented in the class "Throwable".  The fields "errorToken",
     * "expectedTokenSequences", and "tokenImage" do not contain
     * relevant information.  The JavaCC generated code does not use
     * these constructors.
     * 
     * @deprecated
     */
    protected ParseException() {
        super();
        cause = null;
    }

    /**
     * @deprecated Use a constructor to which you can also pass the template, and the end positions.
     */
    public ParseException(String description, int lineNumber, int columnNumber) {
        this(description, (Template) null, lineNumber, columnNumber, null);
    }

    /**
     * @since 2.3.21
     */
    public ParseException(String description, Template template,
            int lineNumber, int columnNumber, int endLineNumber, int endColumnNumber) {
        this(description, template, lineNumber, columnNumber, endLineNumber, endColumnNumber, null);      
    }

    /**
     * @since 2.3.21
     */
    public ParseException(String description, Template template,
            int lineNumber, int columnNumber, int endLineNumber, int endColumnNumber,
            Throwable cause) {
        this(description,
                template == null ? null : template.getName(),
                        lineNumber, columnNumber,
                        endLineNumber, endColumnNumber,
                        cause);      
    }
    
    /**
     * @deprecated Use {@link #ParseException(String, Template, int, int, int, int)} instead, as IDE-s need the end
     * position of the error too.
     * @since 2.3.20
     */
    public ParseException(String description, Template template, int lineNumber, int columnNumber) {
        this(description, template, lineNumber, columnNumber, null);      
    }

    /**
     * @deprecated Use {@link #ParseException(String, Template, int, int, int, int, Throwable)} instead, as IDE-s need
     * the end position of the error too.
     * @since 2.3.20
     */
    public ParseException(String description, Template template, int lineNumber, int columnNumber, Throwable cause) {
        this(description,
                template == null ? null : template.getName(),
                        lineNumber, columnNumber,
                        0, 0,
                        cause);      
    }

    /**
     * @since 2.3.20
     */
    public ParseException(String description, Template template, Token tk) {
        this(description, template, tk, null);
    }

    /**
     * @since 2.3.20
     */
    public ParseException(String description, Template template, Token tk, Throwable cause) {
        this(description,
                template == null ? null : template.getName(),
                        tk.beginLine, tk.beginColumn,
                        tk.endLine, tk.endColumn,
                        cause);
    }

    /**
     * @since 2.3.20
     */
    public ParseException(String description, TemplateObject tobj) {
        this(description, tobj, null);
    }

    /**
     * @since 2.3.20
     */
    public ParseException(String description, TemplateObject tobj, Throwable cause) {
        this(description,
                tobj.getTemplate() == null ? null : tobj.getTemplate().getName(),
                        tobj.beginLine, tobj.beginColumn,
                        tobj.endLine, tobj.endColumn,
                        cause);
    }

    private ParseException(String description, String templateName,
            int lineNumber, int columnNumber,
            int endLineNumber, int endColumnNumber,
            Throwable cause) {
        super(description);  // but we override getMessage, so it will be different
        this.cause = cause;
        this.description = description; 
        this.templateName = templateName;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.endLineNumber = endLineNumber;
        this.endColumnNumber = endColumnNumber;
    }

    /**
     * Should be used internally only; sets the name of the template that contains the error.
     * This is needed as the constructor that JavaCC automatically calls doesn't pass in the template, so we
     * set it somewhere later in an exception handler. 
     */
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
        synchronized (this) {
            messageAndDescriptionRendered = false;
            message = null;
        }
    }

    public Throwable getCause() {
        return cause;
    }

    /**
     * Returns the error location plus the error description.
     * 
     * @see #getDescription()
     * @see #getTemplateName()
     * @see #getLineNumber()
     * @see #getColumnNumber()
     */
    public String getMessage() {
        synchronized (this) {
            if (messageAndDescriptionRendered) return message;
        }
        renderMessageAndDescription();
        synchronized (this) {
            return message;
        }
    }

    private String getDescription() {
        synchronized (this) {
            if (messageAndDescriptionRendered) return description;
        }
        renderMessageAndDescription();
        synchronized (this) {
            return description;
        }
    }
    
    /**
     * Returns the description of the error without error location or source quotations, or {@code null} if there's no
     * description available. This is useful in editors (IDE-s) where the error markers and the editor window itself
     * already carry this information, so it's redundant the repeat in the error dialog.
     */
    public String getEditorMessage() {
        return getDescription();
    }

    /**
     * Returns the name (template-root relative path) of the template whose parsing was failed.
     * Maybe {@code null} if this is a non-stored template. 
     * 
     * @since 2.3.20
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * 1-based line number of the failing section, or 0 is the information is not available.
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * 1-based column number of the failing section, or 0 is the information is not available.
     */
    public int getColumnNumber() {
        return columnNumber;
    }

    /**
     * 1-based line number of the failing section, or 0 is the information is not available.
     */
    public int getEndLineNumber() {
        return endLineNumber;
    }

    /**
     * 1-based column number of the failing section, or 0 is the information is not available.
     */
    public int getEndColumnNumber() {
        return endColumnNumber;
    }

    private void renderMessageAndDescription() {
        String desc = getOrRenderDescription();

        String prefix;
        if (!isInJBossToolsMode()) {
            prefix = "Parsing error "
                    + MessageUtil.formatLocationForSimpleParsingError(templateName, lineNumber, columnNumber)
                    + ":\n";  
        } else {
            prefix = "[col. " + columnNumber + "] ";
        }

        String msg = prefix + desc;
        desc = msg.substring(prefix.length());  // so we reuse the backing char[]

        synchronized (this) {
            message = msg;
            description = desc;
            messageAndDescriptionRendered = true;
        }
    }

    private boolean isInJBossToolsMode() {
        if (jbossToolsMode == null) {
            try {
                jbossToolsMode =
                        ParseException.class.getClassLoader().toString().indexOf(
                                "[org.jboss.ide.eclipse.freemarker:") != -1
                                ? Boolean.TRUE : Boolean.FALSE;
            } catch (Throwable e) {
                jbossToolsMode = Boolean.FALSE;
            }
        }
        return jbossToolsMode.booleanValue();
    }

    /**
     * Returns the description of the error without the error location, or {@code null} if there's no description
     * available.
     */
    private String getOrRenderDescription() {
        synchronized (this) {
            if (description != null) return description;  // When we already have it from the constructor
        }

        String tokenErrDesc;
        if (currentToken != null) {
            tokenErrDesc = getCustomTokenErrorDescription();
            if (tokenErrDesc == null) {
                // The default JavaCC message generation stuff follows.
                StringBuffer expected = new StringBuffer();
                int maxSize = 0;
                for (int i = 0; i < expectedTokenSequences.length; i++) {
                    if (i != 0) {
                        expected.append(eol);
                    }
                    expected.append("    ");
                    if (maxSize < expectedTokenSequences[i].length) {
                        maxSize = expectedTokenSequences[i].length;
                    }
                    for (int j = 0; j < expectedTokenSequences[i].length; j++) {
                        if (j != 0) expected.append(' ');
                        expected.append(tokenImage[expectedTokenSequences[i][j]]);
                    }
                }
                tokenErrDesc = "Encountered \"";
                Token tok = currentToken.next;
                for (int i = 0; i < maxSize; i++) {
                    if (i != 0) tokenErrDesc += " ";
                    if (tok.kind == 0) {
                        tokenErrDesc += tokenImage[0];
                        break;
                    }
                    tokenErrDesc += add_escapes(tok.image);
                    tok = tok.next;
                }
                tokenErrDesc += "\", but ";

                if (expectedTokenSequences.length == 1) {
                    tokenErrDesc += "was expecting:" + eol;
                } else {
                    tokenErrDesc += "was expecting one of:" + eol;
                }
                tokenErrDesc += expected;
            }
        } else {
            tokenErrDesc = null;
        }
        return tokenErrDesc;
    }

    private String getCustomTokenErrorDescription() {
        final Token nextToken = currentToken.next;
        final int kind = nextToken.kind;
        if (kind == EOF) {
            Set/*<String>*/ endNames = new HashSet();
            for (int i = 0; i < expectedTokenSequences.length; i++) {
                int[] sequence = expectedTokenSequences[i];
                for (int j = 0; j < sequence.length; j++) {
                    switch (sequence[j]) {
                    case END_FOREACH:
                        endNames.add( "#foreach");
                        break;
                    case END_LIST:
                        endNames.add( "#list");
                        break;
                    case END_SWITCH:
                        endNames.add( "#switch");
                        break;
                    case END_IF:
                        endNames.add( "#if");
                        break;
                    case END_COMPRESS:
                        endNames.add( "#compress");
                        break;
                    case END_MACRO:
                        endNames.add( "#macro");
                    case END_FUNCTION:
                        endNames.add( "#function");
                        break;
                    case END_TRANSFORM:
                        endNames.add( "#transform");
                        break;
                    case END_ESCAPE:
                        endNames.add( "#escape");
                        break;
                    case END_NOESCAPE:
                        endNames.add( "#noescape");
                        break;
                    case END_ASSIGN:
                        endNames.add( "#assign");
                        break;
                    case END_LOCAL:
                        endNames.add( "#local");
                        break;
                    case END_GLOBAL:
                        endNames.add( "#global");
                        break;
                    case END_ATTEMPT:
                        endNames.add( "#attempt");
                        break;
                    case CLOSE_BRACE:
                        endNames.add( "\"{\"");
                        break;
                    case CLOSE_BRACKET:
                        endNames.add( "\"[\"");
                        break;
                    case CLOSE_PAREN:
                        endNames.add( "\"(\"");
                        break;
                    case UNIFIED_CALL_END:
                        endNames.add( "@...");
                        break;
                    }
                }
            }
            return "Unexpected end of file reached."
                    + (endNames.size() == 0 ? "" : " You have an unclosed " + concatWithOrs(endNames) + ".");
        } else if (kind == END_IF || kind == ELSE_IF || kind == ELSE) {
            return "Unexpected directive, "
                    + StringUtil.jQuote(nextToken)
                    + ". Check whether you have a valid #if-#elseif-#else structure.";
        }
        return null;
    }

    private String concatWithOrs(Set/*<String>*/ endNames) {
        StringBuffer sb = new StringBuffer(); 
        for (Iterator/*<String>*/ it = endNames.iterator(); it.hasNext(); ) {
            String endName = (String) it.next();
            if (sb.length() != 0) {
                sb.append(" or ");
            }
            sb.append(endName);
        }
        return sb.toString();
    }

    /**
     * Used to convert raw characters to their escaped version
     * when these raw version cannot be used as part of an ASCII
     * string literal.
     */
    protected String add_escapes(String str) {
        StringBuffer retval = new StringBuffer();
        char ch;
        for (int i = 0; i < str.length(); i++) {
            switch (str.charAt(i))
            {
            case 0 :
                continue;
            case '\b':
                retval.append("\\b");
                continue;
            case '\t':
                retval.append("\\t");
                continue;
            case '\n':
                retval.append("\\n");
                continue;
            case '\f':
                retval.append("\\f");
                continue;
            case '\r':
                retval.append("\\r");
                continue;
            case '\"':
                retval.append("\\\"");
                continue;
            case '\'':
                retval.append("\\\'");
                continue;
            case '\\':
                retval.append("\\\\");
                continue;
            default:
                if ((ch = str.charAt(i)) < 0x20 || ch > 0x7e) {
                    String s = "0000" + Integer.toString(ch, 16);
                    retval.append("\\u" + s.substring(s.length() - 4, s.length()));
                } else {
                    retval.append(ch);
                }
                continue;
            }
        }
        return retval.toString();
    }

}
