package dk.gtz.graphedit.viewmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.gtz.graphedit.model.ModelRunTarget;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

/**
 * View model representation of a {@link ModelRunTarget}.
 * 
 * @param name                    The name of the runtarget
 * @param command                 The command to run - must be a valid path to
 *                                an executable
 * @param currentWorkingDirectory Where to execute the command from
 * @param runAsShell              When true, will run the command in a shell
 * @param saveBeforeRun           When true, will save the project before
 *                                running the runtarget
 * @param restartAfterRun         When true, will restart the application after
 *                                the runtarget has been run
 * @param arguments               List of command arguments
 * @param environment             List of environment variable assignments
 */
public record ViewModelRunTarget(
		SimpleStringProperty name,
		SimpleStringProperty command,
		SimpleStringProperty currentWorkingDirectory,
		SimpleBooleanProperty runAsShell,
		SimpleBooleanProperty saveBeforeRun,
		SimpleBooleanProperty restartAfterRun,
		SimpleListProperty<StringProperty> arguments,
		SimpleListProperty<ViewModelEnvironmentVariable> environment) {

	/**
	 * Constructs a new viewmodel for runtargets
	 * 
	 * @param name                    The name of the runtarget
	 * @param command                 The command to run - must be a valid path to
	 *                                an executable
	 * @param currentWorkingDirectory Where to execute the command from
	 * @param runAsShell              When true, will run the command in a shell
	 * @param saveBeforeRun           When true, will save the project before
	 *                                running the runtarget
	 * @param restartAfterRun         When true, will restart the application after
	 *                                the runtarget has been run
	 * @param arguments               List of command arguments
	 * @param environment             List of environment variable assignments
	 */
	public ViewModelRunTarget(
			String name,
			String command,
			String currentWorkingDirectory,
			Boolean runAsShell,
			Boolean saveBeforeRun,
			Boolean restartAfterRun,
			List<String> arguments,
			Map<String, String> environment) {
		this(new SimpleStringProperty(name),
				new SimpleStringProperty(command),
				new SimpleStringProperty(currentWorkingDirectory),
				new SimpleBooleanProperty(runAsShell),
				new SimpleBooleanProperty(saveBeforeRun),
				new SimpleBooleanProperty(restartAfterRun == null ? false : restartAfterRun),
				new SimpleListProperty<>(FXCollections.observableArrayList()),
				new SimpleListProperty<>(FXCollections.observableArrayList()));
		for (var argument : arguments)
			this.arguments.add(new SimpleStringProperty(argument));
		for (var env : environment.entrySet())
			this.environment.add(new ViewModelEnvironmentVariable(new SimpleStringProperty(env.getKey()),
					new SimpleStringProperty(env.getValue())));
	}

	/**
	 * Constructs a new viewmodel for runtargets
	 * 
	 * @param runTarget The model equivalent runtarget
	 */
	public ViewModelRunTarget(ModelRunTarget runTarget) {
		this(runTarget.name(),
				runTarget.command(),
				runTarget.cwd(),
				runTarget.runAsShell(),
				runTarget.saveBeforeRun(),
				runTarget.restartAfterRun(),
				new SimpleListProperty<>(FXCollections.observableArrayList()),
				new SimpleMapProperty<>(FXCollections.observableHashMap()));
		for (var argument : runTarget.arguments())
			this.arguments.add(new SimpleStringProperty(argument));
		for (var env : runTarget.environment().entrySet())
			this.environment.add(new ViewModelEnvironmentVariable(new SimpleStringProperty(env.getKey()),
					new SimpleStringProperty(env.getValue())));
	}

	/**
	 * Converts this viewmodel into a model type
	 * 
	 * @return A model version of this runtarget data
	 */
	public ModelRunTarget toModel() {
		var args = new ArrayList<String>(arguments().size());
		for (var arg : arguments())
			args.add(arg.get());
		var envs = new HashMap<String, String>();
		for (var env : environment())
			envs.put(env.key().get(), env.value().get());
		return new ModelRunTarget(
				name().get(),
				command().get(),
				args,
				currentWorkingDirectory().get(),
				runAsShell().get(),
				saveBeforeRun().get(),
				restartAfterRun().get(),
				envs);
	}
}
