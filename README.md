# gdx-editor
3D Scene Editor(And script test including) for Gnoblin Engine

# Setting up
###IntelliJ users:

  Repository contains project files, so, if you have IntelliJIDEA, you just can import these files as existing Intellij project to the workspace.

###Eclipse users(Manual importing):

+ 1) Create new projects for core and for desktop

+ 2) Import files from core/src, desktop/src to the projects

+ 3) Link requred libraries to core and desktop projects(located in lib directory)

+ 4) Res directory also required for proper engine running, it contains standart font.

+ 5) Link core project as dependency to desktop.

+ 6) Set-up run configuration for desktop

#Features
- Terrain editing/texturing
- Models and Textures Import
- Components editing(Looks very weird, but working, handles only public fields)
- Game simulation
- Script parser(For components editing)
