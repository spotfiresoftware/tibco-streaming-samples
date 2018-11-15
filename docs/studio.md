# Using in TIBCO StreamBase Studio&trade;

These samples are kept on-line in github - this allows browsing of the samples on-line,
support of contributions outside of TIBCO engineering and updates outside of the main
product release lifecycle.

The samples can also be imported into TIBCO StreamBase Studio&trade; :

## Using smart import

* Use **Import -> Git -> Projects from Git ( with smart import )** menu option.  
* Specify the github URL ( https://github.com/TIBCOSoftware/tibco-streaming-samples.git 
* Select the branch that corresponds to the product version you are using
* Choose what sample(s) to import :

![smartimport](studioimport.gif)

:warning: This does require the **Git integration for Eclipse - auto-import for Eclipse** to be installed - this can
be installed by :

1.  Help -> Install new software
2.  In the Work with field enter **http://download.eclipse.org/egit/updates-5.1**
3.  Find and select **Git integration for Eclipse - auto-import for Eclipse >= 4.6**
4.  Select next and Finish

## Using TIBCO menu

TBD

## Working with multiple projects

By default, projects are organized in TIBCO StreamBase Studio&trade; in a flat structure :

![flat](flat.png)

With multiple projects ( such as importing all the samples ) this can become confusing.  Use
**Projects Presentation -> Hierarchical** for a more structured organization :

![hierarchical](hierarchical.gif)

## Other useful eclipse plugins

* [Markdown Text Editor](https://marketplace.eclipse.org/content/markdown-text-editor)
* [GitHub Flavored Markdown viewer plugin](https://marketplace.eclipse.org/content/github-flavored-markdown-viewer-plugin)
* [Mylyn WikiText](https://marketplace.eclipse.org/content/mylyn-wikitext)
* [Docker tooling](https://marketplace.eclipse.org/content/eclipse-docker-tooling) ( requires [TM Terminal](https://marketplace.eclipse.org/content/tm-terminal) )
* [Eclipse Copyright Generator](https://jmini.github.io/Eclipse-Copyright-Generator/)
