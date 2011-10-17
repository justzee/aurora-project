package aurora.ide;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

import aurora.ide.builder.AuroraBuilder;

public class AuroraProjectNature implements IProjectNature {

    private IProject           project;
    public static final String ID = AuroraPlugin.PLUGIN_ID + ".auroranature";

    public void configure() throws CoreException {
        IProjectDescription desc = project.getDescription();
        ICommand[] commands = desc.getBuildSpec();

        for (int i = 0; i < commands.length; ++i) {
            if (commands[i].getBuilderName().equals(AuroraBuilder.BUILDER_ID)) {
                return;
            }
        }

        ICommand[] newCommands = new ICommand[commands.length + 1];
        System.arraycopy(commands, 0, newCommands, 0, commands.length);
        ICommand command = desc.newCommand();
        command.setBuilderName(AuroraBuilder.BUILDER_ID);
        newCommands[newCommands.length - 1] = command;
        desc.setBuildSpec(newCommands);
        project.setDescription(desc, null);
    }

    public void deconfigure() throws CoreException {
        IProjectDescription description = getProject().getDescription();
        ICommand[] commands = description.getBuildSpec();
        for (int i = 0; i < commands.length; ++i) {
            if (commands[i].getBuilderName().equals(AuroraBuilder.BUILDER_ID)) {
                ICommand[] newCommands = new ICommand[commands.length - 1];
                System.arraycopy(commands, 0, newCommands, 0, i);
                System.arraycopy(commands, i + 1, newCommands, i, commands.length - i - 1);
                description.setBuildSpec(newCommands);
                project.setDescription(description, null);
                return;
            }
        }
    }

    public IProject getProject() {
        return project;
    }

    public void setProject(IProject project) {
        this.project = project;
    }

    public static void addAuroraNature(IProject project) throws CoreException {
        if (project.hasNature(ID))
            return;
        IProjectDescription description = project.getDescription();
        String[] ids = description.getNatureIds();
        String[] newIds = new String[ids.length + 1];
        System.arraycopy(ids, 0, newIds, 0, ids.length);
        newIds[ids.length] = ID;
        description.setNatureIds(newIds);
        project.setDescription(description, null);
    }

    public static void removeAuroraNature(IProject project) throws CoreException {
        if (!project.hasNature(ID))
            return;
        IProjectDescription description = project.getDescription();
        String[] ids = description.getNatureIds();
        for (int i = 0; i < ids.length; ++i) {
            if (ids[i].equals(ID)) {
                String[] newIds = new String[ids.length - 1];
                System.arraycopy(ids, 0, newIds, 0, i);
                System.arraycopy(ids, i + 1, newIds, i, ids.length - i - 1);
                description.setNatureIds(newIds);
                project.setDescription(description, null);
                return;
            }
        }
    }

    public static boolean hasAuroraNature(IProject project) throws CoreException {
        return project.hasNature(ID);
    }

}
