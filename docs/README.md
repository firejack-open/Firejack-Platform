Platform Documentation
====================

Overview
--------

The source for Platform documentation is here under ``sources/`` in the
form of .rst files. These files use
[reStructuredText](http://docutils.sourceforge.net/rst.html)
formatting with [Sphinx](http://sphinx-doc.org/) extensions for
structure, cross-linking and indexing.

The HTML files are built and hosted on
[readthedocs.org](https://readthedocs.org/projects/platform/).

Getting Started
---------------

To edit and test the docs, you'll need to install the Sphinx tool and
its dependencies. To install Sphinx follow steps provided on [official site](http://sphinx-doc.org/install.html).

# Contributing

## Normal Case:

* Work in your own fork of the code, we accept pull requests.
* Change the ``.rst`` files with your favorite editor -- try to keep the
  lines short and respect RST and Sphinx conventions. 
* Run ``make clean docs`` to clean up old files and generate new ones,
  or just ``make docs`` to update after small changes.
* Your static website can now be found in the ``_build`` directory.
* To preview what you have generated run ``make server`` and open
  http://localhost:8000/ in your favorite browser.

``make clean docs`` must complete without any warnings or errors.

## Special Case for RST Newbies:

If you want to write a new doc or make substantial changes to an
existing doc, but **you don't know RST syntax**, we will accept pull
requests in Markdown and plain text formats. We really want to
encourage people to share their knowledge and don't want the markup
syntax to be the obstacle. So when you make the Pull Request, please
note in your comment that you need RST markup assistance, and we'll
make the changes for you, and then we will make a pull request to your
pull request so that you can get all the changes and learn about the
markup.

Working using GitHub's file editor
----------------------------------

Alternatively, for small changes and typos you might want to use
GitHub's built in file editor. It allows you to preview your changes
right online (though there can be some differences between GitHub
markdown and Sphinx RST). Just be careful not to create many commits.

Images
------

When you need to add images, try to make them as small as possible
(e.g. as gif). Usually images should go in the same directory as the
.rst file which references them, or in a subdirectory if one already
exists.


Guides on using sphinx
----------------------
* To make links to certain sections create a link target like so:

  ```
    .. _hello_world:

    Hello world
    ===========

    This is a reference to :ref:`hello_world` and will work even if we
    move the target to another file or change the title of the section. 
  ```

  The ``_hello_world:`` will make it possible to link to this position
  (page and section heading) from all other pages. See the [Sphinx
  docs](http://sphinx-doc.org/markup/inline.html#role-ref) for more
  information and examples.

* Notes, warnings and alarms

  ```
    # a note (use when something is important)
    .. note::

    # a warning (orange)
    .. warning::

    # danger (red, use sparsely)
    .. danger::


Manpages
--------

* To make the manpages, run ``make man``. Please note there is a bug
  in Sphinx 1.1.3 which makes this fail.  Upgrade to the latest version
  of Sphinx.
