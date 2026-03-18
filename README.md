# Varflix: A Flexible Approach for Variability Mining

The Varflix tool is a flexible tool enabling building UVL variability models from input variants. 
The project is a multi-module maven project with three modules: 
- **varmining**: This module contains all the parts of Varflix necessary to generate variability models, which are Feature Extraction, Constraint Derivation and Variability Model Generation. 
- **datastructures**: Contains all data structures required for the different process steps.
- **variability-gui**: Contains the Graphical User Interface(GUI) components.

A key feature of the approach is continuous and iterative user involvement, which we support with the GUI component.

## Supported Artifacts
Currently, we mainly focus on CPPS control software applications implemented with the IEC 61499 standard.


## License
This project is licensed under the MPL-2.0 License. The license file and all the details can be found in [LICENSE.md](LICENSE.md).
This project includes portions of VariabilityAnalysisGUI, Copyright (c) 2025 Michael Schmidhammer, also licensed under the MPL License.
