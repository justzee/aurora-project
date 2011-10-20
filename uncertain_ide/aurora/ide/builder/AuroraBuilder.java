package aurora.ide.builder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import aurora.ide.builder.validator.BmValidator;
import aurora.ide.builder.validator.ScreenValidator;
import aurora.ide.builder.validator.SvcValidator;
import aurora.ide.builder.validator.UncertainLocalValidator;
import aurora.ide.project.propertypage.ProjectPropertyPage;

public class AuroraBuilder extends IncrementalProjectBuilder {
    private IProgressMonitor monitor;
    private int              filecount   = 0;
    private int              currentfile = 0;

    class SampleDeltaVisitor implements IResourceDeltaVisitor {
        public boolean visit(IResourceDelta delta) throws CoreException {
            IResource resource = delta.getResource();
            switch (delta.getKind()) {
            case IResourceDelta.ADDED:
                validate(resource);
                break;
            case IResourceDelta.REMOVED:
                break;
            case IResourceDelta.CHANGED:
                validate(resource);
                break;
            }
            // return true to continue visiting children.
            return true;
        }
    }

    class SampleResourceVisitor implements IResourceVisitor {
        public boolean visit(IResource resource) {
            validate(resource);
            // return true to continue visiting children.
            return true;
        }
    }

    public static final String BUILDER_ID             = "aurora.ide.auroraBuilder";

    public static final String UNDEFINED_ATTRIBUTE    = "aurora.ide.undefinedAttribute";
    public static final String UNDEFINED_BM           = "aurora.ide.undefinedBM";
    public static final String UNDEFINED_DATASET      = "aurora.ide.undefinedDataSet";
    public static final String UNDEFINED_FOREIGNFIELD = "aurora.ide.undefinedForeignField";
    public static final String UNDEFINED_LOCALFIELD   = "aurora.ide.undefinedLocalField";
    public static final String UNDEFINED_SCREEN       = "aurora.ide.undefinedScreen";
    public static final String NONENAMESPACE          = "aurora.ide.nonenamespace";
    public static final String CONFIG_PROBLEM         = "aurora.ide.configProblem";
    public static final String UNDEFINED_TAG          = "aurora.ide.undefinedTag";
    public static final String FATAL_ERROR            = "aurora.ide.fatalError";

    public static void addMarker(IFile file, String message, int lineNumber, int severity, String markerType) {
        try {
            IMarker marker = file.createMarker(markerType);
            marker.setAttribute(IMarker.MESSAGE, message);
            marker.setAttribute(IMarker.SEVERITY, severity);
            if (lineNumber == -1) {
                lineNumber = 1;
            }
            marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
        } catch (CoreException e) {
        }
    }

    public static void addMarker(IFile file, String message, int line, int start, int length, int severity,
            String markerType) {
        try {
            IMarker marker = file.createMarker(markerType);
            marker.setAttribute(IMarker.MESSAGE, message);
            marker.setAttribute(IMarker.SEVERITY, severity);
            marker.setAttribute(IMarker.LINE_NUMBER, line);
            marker.setAttribute(IMarker.CHAR_START, start);
            marker.setAttribute(IMarker.CHAR_END, start + length);
        } catch (CoreException e) {
        }
    }

    private Set<IFolder> web_infs = new HashSet<IFolder>();

    public static void addMarker(IFile file, String msg, int lineno, IRegion region, int sevrity, String markerType) {
        if (region == null) {
            addMarker(file, msg, lineno, sevrity, markerType);
        } else {
            addMarker(file, msg, lineno, region.getOffset(), region.getLength(), sevrity, markerType);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
     * java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
        if (kind == FULL_BUILD) {
            fullBuild(monitor);
        } else {
            this.monitor = monitor;
            IResourceDelta delta = getDelta(getProject());
            if (delta == null) {
                fullBuild(monitor);
            } else {
                incrementalBuild(delta, monitor);
            }
        }
        return null;
    }

    private void deleteMarkers(IFile file) {
        try {
            file.deleteMarkers(CONFIG_PROBLEM, false, IResource.DEPTH_ZERO);
            file.deleteMarkers(UNDEFINED_ATTRIBUTE, false, IResource.DEPTH_ZERO);
            file.deleteMarkers(UNDEFINED_BM, false, IResource.DEPTH_ZERO);
            file.deleteMarkers(UNDEFINED_DATASET, false, IResource.DEPTH_ZERO);
            file.deleteMarkers(UNDEFINED_FOREIGNFIELD, false, IResource.DEPTH_ZERO);
            file.deleteMarkers(UNDEFINED_LOCALFIELD, false, IResource.DEPTH_ZERO);
            file.deleteMarkers(UNDEFINED_SCREEN, false, IResource.DEPTH_ZERO);
            file.deleteMarkers(NONENAMESPACE, false, IResource.DEPTH_ZERO);
            file.deleteMarkers(UNDEFINED_TAG, false, IResource.DEPTH_ZERO);
            file.deleteMarkers(FATAL_ERROR, false, IResource.DEPTH_ZERO);
        } catch (CoreException ce) {
        }
    }

    protected void fullBuild(final IProgressMonitor monitor) throws CoreException {
        try {
            if (!checkWebDir())
                return;
            filecount = 0;
            currentfile = 0;
            getProject().accept(new IResourceVisitor() {

                public boolean visit(IResource resource) throws CoreException {
                    filecount++;
                    return true;
                }
            });
            this.monitor = monitor;
            monitor.beginTask("builder " + getProject().getName(), filecount);
            getProject().accept(new SampleResourceVisitor());
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) throws CoreException {
        if (!checkWebDir())
            return;
        filecount = 1;
        currentfile = 0;
        monitor.beginTask("builder " + getProject().getName(), filecount);
        delta.accept(new SampleDeltaVisitor());
    }

    private boolean checkWebDir() throws CoreException {
        IProject project = getProject();
        project.deleteMarkers(CONFIG_PROBLEM, false, IResource.DEPTH_ZERO);
        String webdir = project.getPersistentProperty(ProjectPropertyPage.WebQN);
        if (webdir == null || !project.getParent().getFolder(new Path(webdir + "/WEB-INF")).exists()) {
            IMarker marker = project.createMarker(CONFIG_PROBLEM);
            marker.setAttribute(IMarker.MESSAGE, "未指定Web主目录,请打开属性页设置!");
            marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    MessageBox mb = new MessageBox(new Shell(), SWT.ERROR);
                    mb.setText("builder error");
                    mb.setMessage("请先设置Web主目录,请打开属性页设置");
                    mb.open();
                }
            });
            return false;
        }
        return true;
    }

    private void validate(IResource resource) {
        monitor.subTask(resource.getName());
        currentfile++;
        monitor.worked(1);
        if (resource instanceof IFile) {
            IFile file = (IFile) resource;
            deleteMarkers(file);
            String ext = file.getFileExtension().toLowerCase();
            if (file.getName().equalsIgnoreCase("uncertain.local.xml")) {
                new UncertainLocalValidator(file).validate();
            } else if ("bm".equals(ext)) {
                new BmValidator(file).validate();
            } else if ("svc".equals(ext)) {
                new SvcValidator(file).validate();
            } else if ("screen".equals(ext)) {
                new ScreenValidator(file).validate();
            } else if ("config".equals(ext)) {
            }

        } else if (resource instanceof IFolder) {
            IFolder folder = (IFolder) resource;
            try {
                if (folder.getProject().getPersistentProperty(ProjectPropertyPage.WebQN) != null)
                    return;
                folder.deleteMarkers(CONFIG_PROBLEM, false, IResource.DEPTH_ZERO);
                if (folder.getName().equals("WEB-INF")) {
                    web_infs.add(folder);

                    if (web_infs.size() > 1) {
                        for (IFolder f : web_infs) {
                            f.deleteMarkers(CONFIG_PROBLEM, false, IResource.DEPTH_ZERO);
                            IMarker marker = f.createMarker(CONFIG_PROBLEM);
                            marker.setAttribute(IMarker.MESSAGE, "有多个WEB-INF目录!");
                            marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
                        }
                    }
                }
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }
    }
}
