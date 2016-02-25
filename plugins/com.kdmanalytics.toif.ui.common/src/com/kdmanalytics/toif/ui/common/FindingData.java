
package com.kdmanalytics.toif.ui.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Simple finding data. This gives us a common class that can write to the resources.
 * 
 * @author Ken Duck
 *        
 */
public class FindingData implements Comparable<FindingData>
{
    /**
     * Set to false when we have performed a citing. This is used to
     * pop up a warning on the first citing of any Eclipse run.
     */
    private static boolean firstCite = true;

    private IFile resource;
    private String tool;
    private String description;
    private int line;
    private int offset;
    private String cwe;
    private String sfp;

    /**
     * The digest allows us to generate simple and short unique IDs for each finding.
     */
    private static MessageDigest digest;

    static
    {
        try
        {
            digest = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }

    protected FindingData()
    {

    }

    public FindingData(IFile resource, String tool, String description, int line, int offset, String cwe, String sfp)
    {
        setFindingData(resource, tool, description, line, offset, cwe, sfp);
    }

    /**
     * 
     * @param resource
     * @param tool
     * @param description
     * @param line
     * @param offset
     * @param cwe
     * @param sfp
     */
    protected void setFindingData(IFile resource, String tool, String description, int line, int offset, String cwe, String sfp)
    {
        this.resource = resource;
        this.tool = tool;
        this.description = description;
        this.line = line;
        this.offset = offset;
        this.cwe = cwe;
        this.sfp = sfp;
    }


    /**
     * Output a pretty string representation of the finding.
     */
    public String toString()
    {
        return "[" + tool + "] " + resource.toString() + ":" + line + "," + offset + " - {" + sfp + "," + cwe + "} " + description;
    }

    /**
     * 
     * @param hash
     * @return
     */
    private String getHex(byte[] hash)
    {
        StringBuffer hexString = new StringBuffer();

        for (int i = 0; i < hash.length; i++) {
            if ((0xff & hash[i]) < 0x10) {
                hexString.append("0"
                        + Integer.toHexString((0xFF & hash[i])));
            } else {
                hexString.append(Integer.toHexString(0xFF & hash[i]));
            }
        }

        return hexString.toString();
    }
    /** Returns a unique ID for the finding. Uses an MD5 checksum for uniqueness
     * while keeping the value reasonably short.
     * 
     * @return
     */
    public String getId()
    {
        if(digest != null)
        {
            String id = tool + ":" + line + ":" + offset + ":" + cwe + ":" + description;
            return getHex(digest.digest(id.getBytes()));
        }
        else
        {
            // Fall back that should never ever be required. Not necessarily unique.
            String id = tool + ":" + line + ":" + offset + ":" + cwe;
            return id;
        }
    }

    /** Return a unique ID for the type of defect. The format is:
     * 
     *   <tool>:<cwe>
     * 
     * @return
     */
    public String getTypeId()
    {
        return tool + ":" + cwe;
    }


    /** Get the name of the file with the finding
     * 
     * @return
     */
    public String getFileName()
    {
        String name = resource.getName();
        return name;
    }

    /** Get the path to the file with the finding
     * 
     * @return
     */
    public String getPath()
    {
        return resource.getProjectRelativePath().toString();
    }

    /** Get the line number in the file with the finding
     * 
     * @return
     */
    public String getLine() 
    {
        return Integer.toString(line);
    }

    /** Return the line number in the file with the finding
     * 
     * @return
     */
    public int getLineNumber()
    {
        return line;
    }

    /**
     * 
     * @return
     */
    public int getOffset()
    {
        return offset;
    }

    /** Return the line number that KDM reported as being the finding
     * location.
     * 
     * @return
     */
    public int getKdmLine()
    {
        // Currently this is always the same
        return line;
    }

    /** Get the tool that found the finding
     * 
     * @return
     */
    public String getTool()
    {
        return tool;
    }

    /** Get the SFP for the finding
     * 
     * @return
     */
    public String getSfp()
    {
        return sfp;
    }

    /** Get the CWE for the finding
     * 
     * @return
     */
    public String getCwe()
    {
        return cwe;
    }

    /** The trust level for a finding is defined for ALL findings of the same
     * tool/cwe type.
     * 
     * This option ...  sets the level of trust for the selected finding. This
     * level is propagated throughout the data set, marking any finding with the
     * same CWE from the same tool with the specified value. Trust is an indication
     * of how much faith the analyst has in the tools ability to accurately detect
     * the defect.
     * 
     * @param val
     */
    public void setTrust(int val)
    {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        if(val < 0 || val > 100)
        {
            // This should never happen. It is checked at entry time.
            throw new IllegalArgumentException("Trust value [" + val + "] must be >=0 and <=100");
        }
        String type = getTypeId();
        store.setValue(Activator.PLUGIN_ID + ".trust." + type, val);
    }

    /** Get the trust level for the finding
     * 
     * @return
     */
    public int getTrust()
    {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        String type = getTypeId();
        int trust = store.getInt(Activator.PLUGIN_ID + ".trust." + type);
        // The value should always be good, it is checked at entry time.
        if(trust >= 0 && trust <= 100) return trust;
        return 0;
    }


    /** Get the finding description
     * 
     * @return
     */
    public String getDescription()
    {
        return description;
    }

    /** Get the IFile resource
     * 
     * @return
     */
    public IFile getFile()
    {
        return resource;
    }

    /** Cite this finding.
     * 
     * @param b
     */
    public void cite(Boolean b)
    {
        try
        {
            String value = null;
            if(b != null) value = b.toString();
            QualifiedName key = new QualifiedName(Activator.PLUGIN_ID, getId() + ":citing");
            resource.setPersistentProperty(key, value);
            if(b != null)
            {
                ResourceAttributes attrs = resource.getResourceAttributes();
                attrs.setReadOnly(true);
                resource.setResourceAttributes(attrs);

                if(firstCite)
                {
                    firstCite = false;
                    PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                            MessageDialog.openInformation(shell, "Warning", "Warning: Citings are file attributes. Editing or deleting a file will delete its citing information. Daily snapshots of citing information are saved in <project>/.KDM/TOIF/history");
                        }
                    }); 
                }
            }
        }
        catch (CoreException e)
        {
            e.printStackTrace();
        }
    }

    /** Return the citing applied to the finding.
     * 
     * @return
     */
    public Boolean getCiting()
    {
        try
        {
            QualifiedName key = new QualifiedName(Activator.PLUGIN_ID, getId() + ":citing");
            String buf = resource.getPersistentProperty(key);
            if(buf != null)
            {
                boolean b = Boolean.parseBoolean(buf);
                return b;
            }
        }
        catch (CoreException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /** Return a string that contains all text for the finding that we wish to have searchable.
     * The fields are separated by the pipe symbol.
     * 
     * @return
     */
    public String getSearchableText()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(resource.getProjectRelativePath().toString());
        sb.append(" | ");
        sb.append(line);
        sb.append(" | ");
        if(sfp != null && !sfp.isEmpty())
        {
            sb.append(sfp);
            sb.append(" | ");
        }
        if(cwe != null && !cwe.isEmpty())
        {
            sb.append(cwe);
            sb.append(" | ");
        }
        sb.append("trust = ").append(getTrust());
        sb.append(" | ");
        sb.append(tool);
        sb.append(" | ");
        sb.append(description);
        return sb.toString();
    }

    /** In the TOIF adaptor output, the description has this form:
     * 
     *   <ident>:<description>
     *   
     * This returns the identifier.
     * 
     * @return
     */
    public String getCoverageIdentifier()
    {
        if(description != null)
        {
            int index = description.indexOf(':');
            if(index > 0)
            {
                return description.substring(0, index).trim();
            }
        }
        return null;
    }

    /** In the TOIF adaptor output, the description has this form:
     * 
     *   <ident>:<description>
     *   
     * This returns the description.
     * 
     * @return
     */
    public String getCoverageDescription()
    {
        if(description != null)
        {
            if(description.contains(" it is best to do as little as possible in them"))
            {
                System.err.println("DESCRIPTION: " + description);
            }
            int index = description.indexOf(':');
            if(index > 0)
            {
                return description.substring(index + 1).trim();
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return toString().hashCode();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o)
    {
        if(o instanceof FindingData)
        {
            FindingData finding = (FindingData)o;
            if(finding.line != line) return false;
            if(finding.offset != offset) return false;
            if(!finding.resource.equals(resource)) return false;
            if(!finding.tool.equals(tool)) return false;
            if(!finding.description.equals(description)) return false;
            if(!finding.cwe.equals(cwe)) return false;
            if(!finding.sfp.equals(sfp)) return false;
            return true;
        }
        else
        {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(FindingData o)
    {
        if(!resource.equals(o.resource)) return resource.toString().compareTo(o.resource.toString());
        if(!description.equals(o.description)) return description.toString().compareTo(o.description.toString());
        if(!cwe.equals(o.cwe)) return cwe.toString().compareTo(o.cwe.toString());
        if(!sfp.equals(o.sfp)) return sfp.toString().compareTo(o.sfp.toString());
        if(!tool.equals(o.tool)) return tool.toString().compareTo(o.tool.toString());
        if(line != o.line) return o.line - line;
        if(offset != o.offset) return o.offset - offset;
        return 0;
    }
}
